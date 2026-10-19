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
import com.bookit.reservation.history.dto.ReservationHistoryResponse;
import com.bookit.reservation.slot.domain.ReservationSlot;
import com.bookit.reservation.slot.domain.ReservationSlotRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationSlotRepository reservationSlotRepository;
    private final MemberRepository memberRepository;
    private final ReservationHistoryRepository reservationHistoryRepository;

    public ReservationService(
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
    public ReservationResponse reserve(String email, ReservationCreateRequest request) {
        Member member = getMember(email);
        // SELECT ... FOR UPDATE로 슬롯 행을 잠근 뒤 정원을 확인한다.
        // 같은 슬롯의 동시 예약 요청은 여기서 순차적으로 처리된다.
        ReservationSlot slot = getSlotForUpdate(request.slotId());

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

        Reservation reservation = reservationRepository.save(new Reservation(member, slot));
        reservationHistoryRepository.save(new ReservationHistory(reservation, ReservationHistoryType.CREATED));
        return ReservationResponse.from(reservation);
    }

    public List<ReservationResponse> findMyReservations(String email) {
        return reservationRepository.findAllByMemberEmailOrderByCreatedAtDesc(email).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<ReservationHistoryResponse> findHistory(Long reservationId, String email) {
        reservationRepository.findByIdAndMemberEmail(reservationId, email)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));
        return reservationHistoryRepository.findAllByReservationIdOrderByOccurredAtAsc(reservationId).stream()
                .map(ReservationHistoryResponse::from)
                .toList();
    }

    @Transactional
    public void cancel(Long reservationId, String email) {
        Reservation reservation = reservationRepository.findByIdAndMemberEmail(reservationId, email)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));
        reservation.cancel();
        reservationHistoryRepository.save(new ReservationHistory(reservation, ReservationHistoryType.CANCELLED));
    }

    private Member getMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
    }

    private ReservationSlot getSlot(Long slotId) {
        return reservationSlotRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("예약 시간을 찾을 수 없습니다."));
    }

    private ReservationSlot getSlotForUpdate(Long slotId) {
        return reservationSlotRepository.findByIdForUpdate(slotId)
                .orElseThrow(() -> new IllegalArgumentException("예약 시간을 찾을 수 없습니다."));
    }
}
