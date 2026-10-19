package com.bookit.reservation.notification.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationNotificationRepository extends JpaRepository<ReservationNotification, Long> {

    Optional<ReservationNotification> findByReservationId(Long reservationId);

    List<ReservationNotification> findAllByReservationMemberEmailOrderByScheduledAtDesc(String email);

    List<ReservationNotification> findAllByStatusAndScheduledAtLessThanEqual(
            NotificationStatus status,
            LocalDateTime scheduledAt
    );
}
