package com.bookit.reservation.slot.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

public interface ReservationSlotRepository extends JpaRepository<ReservationSlot, Long> {

    List<ReservationSlot> findAllByServiceIdOrderByStartAtAsc(Long serviceId);

    Optional<ReservationSlot> findByIdAndServiceId(Long slotId, Long serviceId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select slot from ReservationSlot slot where slot.id = :slotId")
    Optional<ReservationSlot> findByIdForUpdate(@Param("slotId") Long slotId);
}
