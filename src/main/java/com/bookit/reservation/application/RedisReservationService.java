package com.bookit.reservation.application;

import com.bookit.reservation.dto.ReservationCreateRequest;
import com.bookit.reservation.dto.ReservationResponse;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

@Component
public class RedisReservationService {

    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT = new DefaultRedisScript<>(
            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end",
            Long.class
    );

    private final StringRedisTemplate redisTemplate;
    private final RedisReservationAttemptService attemptService;
    private final long waitMs;
    private final long leaseMs;

    public RedisReservationService(
            StringRedisTemplate redisTemplate,
            RedisReservationAttemptService attemptService,
            @Value("${reservation.redis-lock.wait-ms}") long waitMs,
            @Value("${reservation.redis-lock.lease-ms}") long leaseMs
    ) {
        this.redisTemplate = redisTemplate;
        this.attemptService = attemptService;
        this.waitMs = waitMs;
        this.leaseMs = leaseMs;
    }

    public ReservationResponse reserve(String email, ReservationCreateRequest request) {
        String lockKey = "lock:reservation-slot:" + request.slotId();
        String lockValue = UUID.randomUUID().toString();

        if (!tryLock(lockKey, lockValue)) {
            throw new IllegalStateException("다른 예약 요청을 처리 중입니다. 잠시 후 다시 시도해 주세요.");
        }

        try {
            return attemptService.reserveOnce(email, request);
        } finally {
            unlock(lockKey, lockValue);
        }
    }

    private boolean tryLock(String lockKey, String lockValue) {
        long deadline = System.currentTimeMillis() + waitMs;
        do {
            Boolean acquired = redisTemplate.opsForValue()
                    .setIfAbsent(lockKey, lockValue, Duration.ofMillis(leaseMs));
            if (Boolean.TRUE.equals(acquired)) {
                return true;
            }
            Thread.onSpinWait();
        } while (System.currentTimeMillis() < deadline);
        return false;
    }

    private void unlock(String lockKey, String lockValue) {
        redisTemplate.execute(UNLOCK_SCRIPT, List.of(lockKey), lockValue);
    }
}
