package com.bookit.service.application;

import com.bookit.member.domain.Member;
import com.bookit.member.domain.MemberRepository;
import com.bookit.service.domain.Service;
import com.bookit.service.domain.ServiceRepository;
import com.bookit.service.dto.ServiceCreateRequest;
import com.bookit.service.dto.ServiceResponse;
import com.bookit.service.dto.ServiceUpdateRequest;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final MemberRepository memberRepository;

    public ServiceService(ServiceRepository serviceRepository, MemberRepository memberRepository) {
        this.serviceRepository = serviceRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public ServiceResponse create(String email, ServiceCreateRequest request) {
        Member owner = getMember(email);
        Service service = new Service(owner, request.name(), request.description(), request.durationMinutes(), request.price());
        return ServiceResponse.from(serviceRepository.save(service));
    }

    public List<ServiceResponse> findAll() {
        return serviceRepository.findAllByOrderByCreatedAtDesc().stream().map(ServiceResponse::from).toList();
    }

    public ServiceResponse findById(Long serviceId) {
        return ServiceResponse.from(getService(serviceId));
    }

    @Transactional
    public ServiceResponse update(Long serviceId, String email, ServiceUpdateRequest request) {
        Service service = getService(serviceId);
        verifyOwner(service, email);
        service.update(request.name(), request.description(), request.durationMinutes(), request.price());
        return ServiceResponse.from(service);
    }

    @Transactional
    public void delete(Long serviceId, String email) {
        Service service = getService(serviceId);
        verifyOwner(service, email);
        serviceRepository.delete(service);
    }

    private Member getMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
    }

    private Service getService(Long serviceId) {
        return serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("서비스를 찾을 수 없습니다."));
    }

    private void verifyOwner(Service service, String email) {
        if (!service.getOwner().getEmail().equals(email)) {
            throw new AccessDeniedException("서비스를 수정하거나 삭제할 권한이 없습니다.");
        }
    }
}
