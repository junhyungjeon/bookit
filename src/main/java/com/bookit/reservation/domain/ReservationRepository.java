package com.bookit.reservation.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    long countBySlotIdAndStatus(Long slotId, ReservationStatus status);

    boolean existsByMemberIdAndSlotIdAndStatus(Long memberId, Long slotId, ReservationStatus status);

    List<Reservation> findAllByMemberEmailOrderByCreatedAtDesc(String email);

    Optional<Reservation> findByIdAndMemberEmail(Long reservationId, String email);
}
