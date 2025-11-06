package com.insurance.claimservice.dto;

// This is a simple record to structure the request to the payment-service
public record PayoutRequestDto(
        Double amount,
        Long claimId,
        String username,
        Long policyId


) {}