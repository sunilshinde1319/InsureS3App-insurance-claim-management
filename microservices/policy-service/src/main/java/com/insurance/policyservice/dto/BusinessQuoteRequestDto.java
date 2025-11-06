package com.insurance.policyservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BusinessQuoteRequestDto(
        @NotBlank(message = "Business type is required")
        String businessType,

        @NotNull(message = "Number of employees is required")
        @Min(value = 1, message = "Must have at least 1 employee")
        Integer numberOfEmployees,

        @NotNull(message = "Annual revenue is required")
        @Min(value = 0, message = "Revenue cannot be negative")
        Integer annualRevenue
) {
}