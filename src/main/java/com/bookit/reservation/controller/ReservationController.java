package com.bookit.reservation.controller;

import com.bookit.reservation.application.ReservationService;
import com.bookit.reservation.application.OptimisticReservationService;
import com.bookit.reservation.application.RedisReservationService;
import com.bookit.reservation.dto.ReservationCreateRequest;
import com.bookit.reservation.dto.ReservationResponse;
import com.bookit.reservation.history.dto.ReservationHistoryResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final OptimisticReservationService optimisticReservationService;
    private final RedisReservationService redisReservationService;

    public ReservationController(
            ReservationService reservationService,
            OptimisticReservationService optimisticReservationService,
            RedisReservationService redisReservationService
    ) {
        this.reservationService = reservationService;
        this.optimisticReservationService = optimisticReservationService;
        this.redisReservationService = redisReservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> reserve(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody ReservationCreateRequest request
    ) {
        ReservationResponse response = reservationService.reserve(email, request);
        return ResponseEntity.created(URI.create("/api/reservations/" + response.id())).body(response);
    }

    @PostMapping("/optimistic")
    public ResponseEntity<ReservationResponse> reserveWithOptimisticLock(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody ReservationCreateRequest request
    ) {
        ReservationResponse response = optimisticReservationService.reserve(email, request);
        return ResponseEntity.created(URI.create("/api/reservations/" + response.id())).body(response);
    }

    @PostMapping("/redis")
    public ResponseEntity<ReservationResponse> reserveWithRedisLock(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody ReservationCreateRequest request
    ) {
        ReservationResponse response = redisReservationService.reserve(email, request);
        return ResponseEntity.created(URI.create("/api/reservations/" + response.id())).body(response);
    }

    @GetMapping("/me")
    public List<ReservationResponse> findMyReservations(@AuthenticationPrincipal String email) {
        return reservationService.findMyReservations(email);
    }

    @GetMapping("/{reservationId}/history")
    public List<ReservationHistoryResponse> findHistory(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal String email
    ) {
        return reservationService.findHistory(reservationId, email);
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> cancel(@PathVariable Long reservationId, @AuthenticationPrincipal String email) {
        reservationService.cancel(reservationId, email);
        return ResponseEntity.noContent().build();
    }
}
