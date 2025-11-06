package  com.insurance.claimservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ClaimDto(
        @Positive(message = "A valid policy ID is required")
        String claimNumber,
        @NotBlank(message = "Claim description cannot be blank")
        @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
        String description,
        LocalDate claimDate,
        Long policyId,
        @Positive(message = "Claim amount must be a positive number")
        double claimAmount
) {
}