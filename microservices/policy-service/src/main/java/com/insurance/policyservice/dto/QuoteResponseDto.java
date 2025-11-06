package com.insurance.policyservice.dto;

import java.util.List;


public record QuoteResponseDto(List<PlanDto> plans) {
}