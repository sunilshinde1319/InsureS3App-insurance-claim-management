package com.insurance.claimservice.entity;

public enum ClaimStatus {
    SUBMITTED,
    IN_REVIEW,
    APPROVED,
    DENIED,

    APPROVED_PENDING_PAYOUT,
    PAID,
    PAYMENT_FAILED

}