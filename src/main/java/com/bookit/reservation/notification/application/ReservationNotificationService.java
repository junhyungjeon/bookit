package com.bookit.reservation.notification.application;

import com.bookit.reservation.domain.Reservation;
import com.bookit.reservation.notification.domain.NotificationStatus;
import com.bookit.reservation.notification.domain.ReservationNotification;
import com.bookit.reservation.notification.domain.ReservationNotificationRepository;
import com.bookit.reservation.notification.dto.ReservationNotificationResponse;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class ReservationNotificationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationNotificationService.class);

    private final ReservationNotificationRepository reservationNotificationRepository;

    public ReservationNotificationService(ReservationNotificationRepository reservationNotificationRepository) {
        this.reservationNotificationRepository = reservationNotificationRepository;
    }

    @Transactional
    public void scheduleReminder(Reservation reservation) {
        reservationNotificationRepository.save(new ReservationNotification(reservation));
    }

    @Transactional
    public void cancelScheduledReminder(Long reservationId) {
        reservationNotificationRepository.findByReservationId(reservationId)
                .ifPresent(ReservationNotification::cancel);
    }

    public List<ReservationNotificationResponse> findMyNotifications(String email) {
        return reservationNotificationRepository.findAllByReservationMemberEmailOrderByScheduledAtDesc(email).stream()
                .map(ReservationNotificationResponse::from)
                .toList();
    }

    @Scheduled(fixedDelayString = "${reservation.reminder.fixed-delay-ms:60000}")
    @Transactional
    public void sendDueReminders() {
        List<ReservationNotification> notifications = reservationNotificationRepository
                .findAllByStatusAndScheduledAtLessThanEqual(NotificationStatus.SCHEDULED, LocalDateTime.now());

        for (ReservationNotification notification : notifications) {
            notification.send();
            log.info("Reservation reminder sent. reservationId={}, notificationId={}",
                    notification.getReservation().getId(), notification.getId());
        }
    }
}
