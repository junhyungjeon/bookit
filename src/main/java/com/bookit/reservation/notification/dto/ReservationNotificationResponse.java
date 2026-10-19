package com.bookit.reservation.notification.dto;

import com.bookit.reservation.notification.domain.ReservationNotification;
import com.bookit.reservation.notification.domain.NotificationStatus;
import com.bookit.reservation.notification.domain.NotificationType;
import java.time.LocalDateTime;

public record ReservationNotificationResponse(
        Long id,
        Long reservationId,
        NotificationType type,
        NotificationStatus status,
        LocalDateTime scheduledAt,
        LocalDateTime sentAt
) {
    public static ReservationNotificationResponse from(ReservationNotification notification) {
        return new ReservationNotificationResponse(
                notification.getId(),
                notification.getReservation().getId(),
                notification.getType(),
                notification.getStatus(),
                notification.getScheduledAt(),
                notification.getSentAt()
        );
    }
}
