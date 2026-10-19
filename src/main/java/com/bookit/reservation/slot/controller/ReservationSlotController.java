package com.bookit.reservation.slot.controller;

import com.bookit.reservation.slot.application.ReservationSlotService;
import com.bookit.reservation.slot.dto.ReservationSlotCreateRequest;
import com.bookit.reservation.slot.dto.ReservationSlotResponse;
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
@RequestMapping("/api/services/{serviceId}/slots")
public class ReservationSlotController {

    private final ReservationSlotService reservationSlotService;

    public ReservationSlotController(ReservationSlotService reservationSlotService) {
        this.reservationSlotService = reservationSlotService;
    }

    @PostMapping
    public ResponseEntity<ReservationSlotResponse> create(
            @PathVariable Long serviceId,
            @AuthenticationPrincipal String email,
            @Valid @RequestBody ReservationSlotCreateRequest request
    ) {
        ReservationSlotResponse response = reservationSlotService.create(serviceId, email, request);
        return ResponseEntity.created(URI.create("/api/services/" + serviceId + "/slots/" + response.id())).body(response);
    }

    @GetMapping
    public List<ReservationSlotResponse> findAll(@PathVariable Long serviceId) {
        return reservationSlotService.findAllByService(serviceId);
    }

    @DeleteMapping("/{slotId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long serviceId,
            @PathVariable Long slotId,
            @AuthenticationPrincipal String email
    ) {
        reservationSlotService.delete(serviceId, slotId, email);
        return ResponseEntity.noContent().build();
    }
}
