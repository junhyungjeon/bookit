package com.bookit.reservation.dto;

import com.bookit.reservation.domain.Reservation;
import com.bookit.reservation.domain.ReservationStatus;
import java.time.LocalDateTime;

public record ReservationResponse(
        Long id,
        Long slotId,
        Long serviceId,
        String serviceName,
        LocalDateTime startAt,
        LocalDateTime endAt,
        ReservationStatus status,
        LocalDateTime createdAt,
        LocalDateTime cancelledAt
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getSlot().getId(),
                reservation.getSlot().getService().getId(),
                reservation.getSlot().getService().getName(),
                reservation.getSlot().getStartAt(),
                reservation.getSlot().getEndAt(),
                reservation.getStatus(),
                reservation.getCreatedAt(),
                reservation.getCancelledAt());
    }
}
