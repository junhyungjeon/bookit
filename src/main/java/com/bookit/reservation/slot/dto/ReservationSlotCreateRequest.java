package com.bookit.reservation.slot.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ReservationSlotCreateRequest(
        @NotNull @Future LocalDateTime startAt,
        @NotNull @Future LocalDateTime endAt,
        @NotNull @Min(1) Integer capacity
) {
}
