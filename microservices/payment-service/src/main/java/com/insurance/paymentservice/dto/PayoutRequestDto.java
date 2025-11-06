package com.insurance.paymentservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PayoutRequestDto(
        @NotNull @Positive Double amount,
        @NotNull Long claimId,
        @NotNull String username,
        @NotNull Long policyId
) {}