package com.bookit.reservation;

import static org.assertj.core.api.Assertions.assertThat;

import com.bookit.member.domain.Member;
import com.bookit.member.domain.MemberRepository;
import com.bookit.reservation.application.OptimisticReservationService;
import com.bookit.reservation.domain.ReservationRepository;
import com.bookit.reservation.domain.ReservationStatus;
import com.bookit.reservation.dto.ReservationCreateRequest;
import com.bookit.reservation.history.domain.ReservationHistoryRepository;
import com.bookit.reservation.slot.domain.ReservationSlot;
import com.bookit.reservation.slot.domain.ReservationSlotRepository;
import com.bookit.service.domain.Service;
import com.bookit.service.domain.ServiceRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class OptimisticReservationConcurrencyTest {

    @Autowired private OptimisticReservationService optimisticReservationService;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private ReservationHistoryRepository reservationHistoryRepository;
    @Autowired private ReservationSlotRepository reservationSlotRepository;
    @Autowired private ServiceRepository serviceRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @AfterEach
    void cleanUp() {
        reservationHistoryRepository.deleteAll();
        reservationRepository.deleteAll();
        reservationSlotRepository.deleteAll();
        serviceRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    void onlyOneReservationSucceedsWithOptimisticLockForTheLastSeat() throws Exception {
        Member owner = memberRepository.save(new Member("owner-opt@bookit.com", passwordEncoder.encode("password123"), "운영자"));
        Member firstMember = memberRepository.save(new Member("first-opt@bookit.com", passwordEncoder.encode("password123"), "첫번째"));
        Member secondMember = memberRepository.save(new Member("second-opt@bookit.com", passwordEncoder.encode("password123"), "두번째"));
        Service service = serviceRepository.save(new Service(owner, "낙관적 락 테스트", "마지막 한 자리 테스트", 60, 10000));
        ReservationSlot slot = reservationSlotRepository.save(new ReservationSlot(
                service, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1), 1));

        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        List<Future<Boolean>> results = List.of(
                executor.submit(() -> reserveAtSameTime(firstMember.getEmail(), slot.getId(), ready, start)),
                executor.submit(() -> reserveAtSameTime(secondMember.getEmail(), slot.getId(), ready, start))
        );

        ready.await();
        start.countDown();

        int successCount = 0;
        for (Future<Boolean> result : results) {
            if (result.get()) {
                successCount++;
            }
        }
        executor.shutdown();

        assertThat(successCount).isEqualTo(1);
        assertThat(reservationRepository.countBySlotIdAndStatus(slot.getId(), ReservationStatus.CONFIRMED)).isEqualTo(1);
    }

    private boolean reserveAtSameTime(String email, Long slotId, CountDownLatch ready, CountDownLatch start) {
        try {
            ready.countDown();
            start.await();
            optimisticReservationService.reserve(email, new ReservationCreateRequest(slotId));
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
