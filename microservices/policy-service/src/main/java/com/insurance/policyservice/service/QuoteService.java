package com.insurance.policyservice.service;

import com.insurance.policyservice.dto.*;
import com.insurance.policyservice.entity.PolicyPlan;
import com.insurance.policyservice.repository.PolicyPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuoteService {

    @Autowired
    private PolicyPlanRepository policyPlanRepository;

    /**
     * Calculates quotes for Auto Insurance.
     * Fetches base plans from DB and applies a risk factor based on vehicle and driver details.
     */
    public QuoteResponseDto calculateAutoQuote(AutoQuoteRequestDto request) {
        List<PolicyPlan> plans = policyPlanRepository.findByPolicyType("Auto");

        double calculatedRiskFactor = 1.0;
        int currentYear = 2025;

        if (request.vehicleYear() > currentYear - 5) calculatedRiskFactor *= 1.2;
        if (request.vehicleYear() < 2005) calculatedRiskFactor *= 1.15;
        if (request.driverAge() < 25) calculatedRiskFactor *= 1.5;
        if (request.drivingExperienceYears() < 2) calculatedRiskFactor *= 1.3;

        // Assign the final calculated value to a new, effectively final variable
        final double finalRiskFactor = calculatedRiskFactor;

        List<PlanDto> calculatedPlans = plans.stream()
                .map(plan -> new PlanDto(
                        plan.getPlanName(),
                        plan.getDescription(),
                        plan.getBaseCoverage(),
                        Math.round(plan.getBasePremium() * finalRiskFactor) // Use the final variable here
                ))
                .collect(Collectors.toList());

        return new QuoteResponseDto(calculatedPlans);
    }

    /**
     * Calculates quotes for Home Insurance.
     * Fetches base plans from DB and applies a risk factor based on property details.
     */
    public QuoteResponseDto calculateHomeQuote(HomeQuoteRequestDto request) {
        List<PolicyPlan> plans = policyPlanRepository.findByPolicyType("Home");

        double calculatedRiskFactor = 1.0;
        calculatedRiskFactor += (request.propertyValue() / 5000000.0);
        if (request.homeAgeInYears() > 50) calculatedRiskFactor *= 1.4;
        if ("Urban".equalsIgnoreCase(request.location())) calculatedRiskFactor *= 1.1;

        final double finalRiskFactor = calculatedRiskFactor;

        List<PlanDto> calculatedPlans = plans.stream()
                .map(plan -> new PlanDto(
                        plan.getPlanName(),
                        plan.getDescription(),
                        plan.getBaseCoverage(),
                        Math.round(plan.getBasePremium() * finalRiskFactor)
                ))
                .collect(Collectors.toList());

        return new QuoteResponseDto(calculatedPlans);
    }

    /**
     * Calculates quotes for Health Insurance.
     * Fetches base plans from DB and applies a risk factor based on age and medical history.
     */
    public QuoteResponseDto calculateHealthQuote(HealthQuoteRequestDto request) {
        List<PolicyPlan> plans = policyPlanRepository.findByPolicyType("Health");

        double calculatedRiskFactor = 1.0;
        if (request.applicantAge() > 60) calculatedRiskFactor *= 2.5;
        else if (request.applicantAge() > 40) calculatedRiskFactor *= 1.5;

        if ("Fair".equalsIgnoreCase(request.medicalHistory())) calculatedRiskFactor *= 1.3;
        else if ("Excellent".equalsIgnoreCase(request.medicalHistory())) calculatedRiskFactor *= 0.9;

        final double finalRiskFactor = calculatedRiskFactor;

        List<PlanDto> calculatedPlans = plans.stream()
                .map(plan -> new PlanDto(
                        plan.getPlanName(),
                        plan.getDescription(),
                        plan.getBaseCoverage(),
                        Math.round(plan.getBasePremium() * finalRiskFactor)
                ))
                .collect(Collectors.toList());

        return new QuoteResponseDto(calculatedPlans);
    }

    /**
     * Calculates a quote for Life Insurance.
     */
    public QuoteResponseDto calculateLifeQuote(LifeQuoteRequestDto request) {
        PolicyPlan planTemplate = policyPlanRepository.findByPolicyType("Life").stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Life insurance plan not configured in database."));

        double tempPremium = (request.coverageAmount() / 100000.0) * planTemplate.getBasePremium();
        if (request.applicantAge() > 50) tempPremium *= 2.0;
        if ("20 Years".equals(request.termLength())) tempPremium *= 1.8;
        else if ("Whole Life".equals(request.termLength())) tempPremium *= 2.5;

        final double finalPremium = tempPremium;

        PlanDto finalPlan = new PlanDto(
                planTemplate.getPlanName(),
                planTemplate.getDescription(),
                request.coverageAmount(),
                Math.round(finalPremium)
        );

        return new QuoteResponseDto(List.of(finalPlan));
    }

    /**
     * Calculates quotes for Business Insurance.
     */
    public QuoteResponseDto calculateBusinessQuote(BusinessQuoteRequestDto request) {
        List<PolicyPlan> plans = policyPlanRepository.findByPolicyType("Business");

        double calculatedRiskFactor = 1.0;
        calculatedRiskFactor += (request.numberOfEmployees() / 10.0);
        calculatedRiskFactor += (request.annualRevenue() / 5000000.0);
        if ("Construction".equalsIgnoreCase(request.businessType())) calculatedRiskFactor *= 1.5;

        final double finalRiskFactor = calculatedRiskFactor;

        List<PlanDto> calculatedPlans = plans.stream()
                .map(plan -> new PlanDto(
                        plan.getPlanName(),
                        plan.getDescription(),
                        plan.getBaseCoverage(),
                        Math.round(plan.getBasePremium() * finalRiskFactor)
                ))
                .collect(Collectors.toList());

        return new QuoteResponseDto(calculatedPlans);
    }

    /**
     * Calculates quotes for Travel Insurance.
     */
    public QuoteResponseDto calculateTravelQuote(TravelQuoteRequestDto request) {
        List<PolicyPlan> plans = policyPlanRepository.findByPolicyType("Travel");

        double calculatedRiskFactor = 1.0;
        calculatedRiskFactor += (request.tripDurationInDays() / 10.0);
        if (request.travelerAge() > 65) calculatedRiskFactor *= 1.8;
        if ("Worldwide".equalsIgnoreCase(request.destination())) calculatedRiskFactor *= 1.2;

        final double finalRiskFactor = calculatedRiskFactor;

        List<PlanDto> calculatedPlans = plans.stream()
                .map(plan -> new PlanDto(
                        plan.getPlanName(),
                        plan.getDescription(),
                        plan.getBaseCoverage(),
                        Math.round(plan.getBasePremium() * finalRiskFactor)
                ))
                .collect(Collectors.toList());

        return new QuoteResponseDto(calculatedPlans);
    }
}