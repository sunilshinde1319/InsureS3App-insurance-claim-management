package com.insurance.policyservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PolicyTypeDto(
        @NotBlank String name,
        @NotBlank String iconName,
        @NotBlank String description,
        @NotNull Boolean isEnabled
) {}