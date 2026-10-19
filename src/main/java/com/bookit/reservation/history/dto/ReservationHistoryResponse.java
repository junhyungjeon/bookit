package com.bookit.reservation.history.dto;

import com.bookit.reservation.history.domain.ReservationHistory;
import com.bookit.reservation.history.domain.ReservationHistoryType;
import java.time.LocalDateTime;

public record ReservationHistoryResponse(Long id, ReservationHistoryType type, LocalDateTime occurredAt) {

    public static ReservationHistoryResponse from(ReservationHistory history) {
        return new ReservationHistoryResponse(history.getId(), history.getType(), history.getOccurredAt());
    }
}
