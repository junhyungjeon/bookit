package com.bookit.reservation.dto;

import jakarta.validation.constraints.NotNull;

public record ReservationCreateRequest(@NotNull Long slotId) {
}
