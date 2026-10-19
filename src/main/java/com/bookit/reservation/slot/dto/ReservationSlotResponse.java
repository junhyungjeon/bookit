package com.bookit.reservation.slot.dto;

import com.bookit.reservation.slot.domain.ReservationSlot;
import java.time.LocalDateTime;

public record ReservationSlotResponse(
        Long id,
        Long serviceId,
        LocalDateTime startAt,
        LocalDateTime endAt,
        int capacity,
        long reservedCount,
        long remainingCapacity,
        boolean available
) {
    public static ReservationSlotResponse from(ReservationSlot slot, long reservedCount) {
        long remainingCapacity = Math.max(slot.getCapacity() - reservedCount, 0);
        return new ReservationSlotResponse(
                slot.getId(),
                slot.getService().getId(),
                slot.getStartAt(),
                slot.getEndAt(),
                slot.getCapacity(),
                reservedCount,
                remainingCapacity,
                remainingCapacity > 0);
    }
}
