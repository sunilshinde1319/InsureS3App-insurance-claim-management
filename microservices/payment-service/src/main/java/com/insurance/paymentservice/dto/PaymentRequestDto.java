package com.insurance.paymentservice.dto;


import jakarta.validation.constraints.Positive;
import org.antlr.v4.runtime.misc.NotNull;

public record PaymentRequestDto(
        @NotNull @Positive Double amount,
        @NotNull Long policyId
) {}