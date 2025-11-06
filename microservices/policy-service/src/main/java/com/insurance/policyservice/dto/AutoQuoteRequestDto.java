package com.insurance.policyservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

public record AutoQuoteRequestDto(
        @NotNull(message = "Vehicle year is required")
        @Min(value = 1980, message = "Vehicle must be from 1980 or newer")
        @Max(value = 2025, message = "Vehicle year cannot be in the future")
        int vehicleYear,

        @NotBlank(message = "Vehicle model cannot be blank")
        String vehicleModel,

        @NotNull(message = "Driver age is required")
        @Min(value = 18, message = "Driver must be at least 18 years old")
        int driverAge, // Use primitive int

        @NotNull(message = "Driving experience is required")
        @Min(value = 0, message = "Driving experience cannot be negative")
        int drivingExperienceYears
) {

        @JsonCreator
        public AutoQuoteRequestDto(
                @JsonProperty("vehicleYear") Integer vehicleYear,
                @JsonProperty("vehicleModel") String vehicleModel,
                @JsonProperty("driverAge") Integer driverAge,
                @JsonProperty("drivingExperienceYears") Integer drivingExperienceYears
        ) {
                this(
                        vehicleYear == null ? 0 : vehicleYear,
                        vehicleModel,
                        driverAge == null ? 0 : driverAge,
                        drivingExperienceYears == null ? 0 : drivingExperienceYears
                );
        }
}