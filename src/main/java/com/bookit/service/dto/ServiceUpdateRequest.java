package com.bookit.service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ServiceUpdateRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 1000) String description,
        @NotNull @Min(10) Integer durationMinutes,
        @NotNull @Min(0) Integer price
) {
}
