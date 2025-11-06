package com.insurance.policyservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TravelQuoteRequestDto(
        @NotBlank(message = "Destination is required")
        String destination,

        @NotNull(message = "Trip duration is required")
        @Min(value = 1, message = "Trip must be at least 1 day")
        Integer tripDurationInDays,

        @NotNull(message = "Traveler age is required")
        @Min(value = 0, message = "Age cannot be negative")
        Integer travelerAge
) {
}