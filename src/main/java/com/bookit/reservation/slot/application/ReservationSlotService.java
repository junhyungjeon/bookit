package com.bookit.reservation.slot.application;

import com.bookit.reservation.slot.domain.ReservationSlot;
import com.bookit.reservation.slot.domain.ReservationSlotRepository;
import com.bookit.reservation.slot.dto.ReservationSlotCreateRequest;
import com.bookit.reservation.slot.dto.ReservationSlotResponse;
import com.bookit.service.domain.Service;
import com.bookit.service.domain.ServiceRepository;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class ReservationSlotService {

    private final ReservationSlotRepository reservationSlotRepository;
    private final ServiceRepository serviceRepository;

    public ReservationSlotService(ReservationSlotRepository reservationSlotRepository, ServiceRepository serviceRepository) {
        this.reservationSlotRepository = reservationSlotRepository;
        this.serviceRepository = serviceRepository;
    }

    @Transactional
    public ReservationSlotResponse create(Long serviceId, String email, ReservationSlotCreateRequest request) {
        Service service = getService(serviceId);
        verifyOwner(service, email);

        if (!request.endAt().isAfter(request.startAt())) {
            throw new IllegalArgumentException("종료 시간은 시작 시간보다 늦어야 합니다.");
        }

        ReservationSlot slot = new ReservationSlot(service, request.startAt(), request.endAt(), request.capacity());
        return ReservationSlotResponse.from(reservationSlotRepository.save(slot));
    }

    public List<ReservationSlotResponse> findAllByService(Long serviceId) {
        return reservationSlotRepository.findAllByServiceIdOrderByStartAtAsc(serviceId).stream()
                .map(ReservationSlotResponse::from)
                .toList();
    }

    @Transactional
    public void delete(Long serviceId, Long slotId, String email) {
        Service service = getService(serviceId);
        verifyOwner(service, email);
        ReservationSlot slot = reservationSlotRepository.findByIdAndServiceId(slotId, serviceId)
                .orElseThrow(() -> new IllegalArgumentException("예약 시간을 찾을 수 없습니다."));
        reservationSlotRepository.delete(slot);
    }

    private Service getService(Long serviceId) {
        return serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("서비스를 찾을 수 없습니다."));
    }

    private void verifyOwner(Service service, String email) {
        if (!service.getOwner().getEmail().equals(email)) {
            throw new AccessDeniedException("예약 시간을 관리할 권한이 없습니다.");
        }
    }
}
