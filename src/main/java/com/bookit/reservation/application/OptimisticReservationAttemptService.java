package com.bookit.reservation.application;

import com.bookit.member.domain.Member;
import com.bookit.member.domain.MemberRepository;
import com.bookit.reservation.domain.Reservation;
import com.bookit.reservation.domain.ReservationRepository;
import com.bookit.reservation.domain.ReservationStatus;
import com.bookit.reservation.dto.ReservationCreateRequest;
import com.bookit.reservation.dto.ReservationResponse;
import com.bookit.reservation.history.domain.ReservationHistory;
import com.bookit.reservation.history.domain.ReservationHistoryRepository;
import com.bookit.reservation.history.domain.ReservationHistoryType;
import com.bookit.reservation.slot.domain.ReservationSlot;
import com.bookit.reservation.slot.domain.ReservationSlotRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OptimisticReservationAttemptService {

    private final ReservationRepository reservationRepository;
    private final ReservationSlotRepository reservationSlotRepository;
    private final MemberRepository memberRepository;
    private final ReservationHistoryRepository reservationHistoryRepository;

    public OptimisticReservationAttemptService(
            ReservationRepository reservationRepository,
            ReservationSlotRepository reservationSlotRepository,
            MemberRepository memberRepository,
            ReservationHistoryRepository reservationHistoryRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationSlotRepository = reservationSlotRepository;
        this.memberRepository = memberRepository;
        this.reservationHistoryRepository = reservationHistoryRepository;
    }

    @Transactional
    public ReservationResponse reserveOnce(String email, ReservationCreateRequest request) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        ReservationSlot slot = reservationSlotRepository.findById(request.slotId())
                .orElseThrow(() -> new IllegalArgumentException("예약 시간을 찾을 수 없습니다."));

        if (!slot.getStartAt().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("이미 시작된 예약 시간입니다.");
        }
        if (reservationRepository.existsByMemberIdAndSlotIdAndStatus(
                member.getId(), slot.getId(), ReservationStatus.CONFIRMED)) {
            throw new IllegalStateException("이미 예약한 시간입니다.");
        }

        long reservedCount = reservationRepository.countBySlotIdAndStatus(slot.getId(), ReservationStatus.CONFIRMED);
        if (reservedCount >= slot.getCapacity()) {
            throw new IllegalStateException("예약 가능한 자리가 없습니다.");
        }

        // 슬롯을 변경 상태로 만들어 @Version 비교가 일어나도록 한다.
        slot.recordReservationAttempt();
        Reservation reservation = reservationRepository.save(new Reservation(member, slot));
        reservationHistoryRepository.save(new ReservationHistory(reservation, ReservationHistoryType.CREATED));
        return ReservationResponse.from(reservation);
    }
}
