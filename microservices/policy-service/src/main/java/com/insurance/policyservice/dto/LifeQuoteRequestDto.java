package com.insurance.policyservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LifeQuoteRequestDto(
        @NotNull(message = "Applicant age is required")
        @Min(value = 18, message = "Applicant must be at least 18")
        Integer applicantAge,

        @NotNull(message = "Coverage amount is required")
        @Min(value = 50000, message = "Minimum coverage is $50,000")
        Integer coverageAmount,

        @NotBlank(message = "Term length is required")
        String termLength
) {
}