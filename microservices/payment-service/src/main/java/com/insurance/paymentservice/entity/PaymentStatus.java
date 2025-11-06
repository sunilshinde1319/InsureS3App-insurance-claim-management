package com.insurance.paymentservice.entity;

public enum PaymentStatus {
    PENDING,    // For user-to-company premium payments
    SUCCEEDED,  // For successful premium payments
    FAILED,     // For failed premium payments

    PAYOUT_PROCESSING, // For company-to-user claim payouts
    PAYOUT_SUCCEEDED,
    PAYOUT_FAILED
}