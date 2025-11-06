package com.insurance.policyservice.dto;


public record PlanDto(
        String planName,
        String description,
        double coverageAmount,
        double premium
) {
}