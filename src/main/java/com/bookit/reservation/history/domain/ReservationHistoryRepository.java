package com.bookit.reservation.history.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationHistoryRepository extends JpaRepository<ReservationHistory, Long> {

    List<ReservationHistory> findAllByReservationIdOrderByOccurredAtAsc(Long reservationId);
}
