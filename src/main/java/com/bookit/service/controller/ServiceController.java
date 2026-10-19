package com.bookit.service.controller;

import com.bookit.service.application.ServiceService;
import com.bookit.service.dto.ServiceCreateRequest;
import com.bookit.service.dto.ServiceResponse;
import com.bookit.service.dto.ServiceUpdateRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @PostMapping
    public ResponseEntity<ServiceResponse> create(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody ServiceCreateRequest request
    ) {
        ServiceResponse response = serviceService.create(email, request);
        return ResponseEntity.created(URI.create("/api/services/" + response.id())).body(response);
    }

    @GetMapping
    public List<ServiceResponse> findAll() {
        return serviceService.findAll();
    }

    @GetMapping("/{serviceId}")
    public ServiceResponse findById(@PathVariable Long serviceId) {
        return serviceService.findById(serviceId);
    }

    @PutMapping("/{serviceId}")
    public ServiceResponse update(
            @PathVariable Long serviceId,
            @AuthenticationPrincipal String email,
            @Valid @RequestBody ServiceUpdateRequest request
    ) {
        return serviceService.update(serviceId, email, request);
    }

    @DeleteMapping("/{serviceId}")
    public ResponseEntity<Void> delete(@PathVariable Long serviceId, @AuthenticationPrincipal String email) {
        serviceService.delete(serviceId, email);
        return ResponseEntity.noContent().build();
    }
}
