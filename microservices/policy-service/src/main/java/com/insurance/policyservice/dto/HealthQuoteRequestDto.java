package com.insurance.policyservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record HealthQuoteRequestDto(
        @NotNull(message = "Applicant age is required")
        @Min(value = 18, message = "Applicant must be at least 18")
        Integer applicantAge,

        @NotBlank(message = "Medical history cannot be blank")
        String medicalHistory
) {
}