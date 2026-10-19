package com.bookit.service.dto;

import com.bookit.service.domain.Service;
import java.time.LocalDateTime;

public record ServiceResponse(
        Long id,
        Long ownerId,
        String name,
        String description,
        int durationMinutes,
        int price,
        LocalDateTime createdAt
) {
    public static ServiceResponse from(Service service) {
        return new ServiceResponse(
                service.getId(),
                service.getOwner().getId(),
                service.getName(),
                service.getDescription(),
                service.getDurationMinutes(),
                service.getPrice(),
                service.getCreatedAt());
    }
}
