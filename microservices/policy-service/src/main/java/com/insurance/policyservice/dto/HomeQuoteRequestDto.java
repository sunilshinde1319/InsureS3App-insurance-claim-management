package com.insurance.policyservice.dto;

import jakarta.validation.constraints.*;

public record HomeQuoteRequestDto(
        @NotNull(message = "Property value is required")
        @Positive(message = "Property value must be positive")
        Integer propertyValue,

        @NotNull(message = "Home age is required")
        @Min(value = 0, message = "Home age cannot be negative")
        Integer homeAgeInYears,

        @NotBlank(message = "Location cannot be blank")
        String location
) {
}