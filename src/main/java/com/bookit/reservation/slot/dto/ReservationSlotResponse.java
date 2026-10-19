package com.bookit.reservation.slot.dto;

import com.bookit.reservation.slot.domain.ReservationSlot;
import java.time.LocalDateTime;

public record ReservationSlotResponse(
        Long id,
        Long serviceId,
        LocalDateTime startAt,
        LocalDateTime endAt,
        int capacity
) {
    public static ReservationSlotResponse from(ReservationSlot slot) {
        return new ReservationSlotResponse(
                slot.getId(),
                slot.getService().getId(),
                slot.getStartAt(),
                slot.getEndAt(),
                slot.getCapacity());
    }
}
