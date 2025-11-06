package com.insurance.policyservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record PolicyDto(
        String policyNumber,
        @NotBlank(message = "Policy type cannot be blank")
        String policyType,

        @Positive(message = "Coverage amount must be a positive number")
        double coverageAmount,
        @Positive(message = "Premium must be a positive number")
        double premium,
        LocalDate startDate,
        LocalDate endDate
) {
}