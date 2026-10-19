package com.bookit.reservation.application;

import com.bookit.reservation.dto.ReservationCreateRequest;
import com.bookit.reservation.dto.ReservationResponse;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Component
public class OptimisticReservationService {

    private static final int MAX_RETRIES = 3;

    private final OptimisticReservationAttemptService attemptService;

    public OptimisticReservationService(OptimisticReservationAttemptService attemptService) {
        this.attemptService = attemptService;
    }

    public ReservationResponse reserve(String email, ReservationCreateRequest request) {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                return attemptService.reserveOnce(email, request);
            } catch (ObjectOptimisticLockingFailureException exception) {
                // 다른 요청이 먼저 슬롯 버전을 변경했으므로 최신 상태로 다시 확인한다.
            }
        }
        throw new IllegalStateException("동시 예약 처리에 실패했습니다. 다시 시도해 주세요.");
    }
}
