package com.insurance.policyservice.controller;

import com.insurance.policyservice.dto.*;
import com.insurance.policyservice.entity.Policy;
import com.insurance.policyservice.service.PolicyService;
import com.insurance.policyservice.service.QuoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map; // <-- NEW
import java.util.UUID;
@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    @Autowired
    private PolicyService policyService;

    @Autowired
    private QuoteService quoteService;

    @PostMapping
    public ResponseEntity<Policy> createPolicy(@Valid @RequestBody PolicyDto policyDto,
                                               @RequestParam(required = false) String pendingPolicyId, // <-- NEW PARAM
                                               Authentication authentication) {
        String username = getUsernameFromAuthentication(authentication);
        Policy newPolicy = policyService.createPolicy(policyDto, username, pendingPolicyId);
        return new ResponseEntity<>(newPolicy, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Policy>> getMyPolicies(Authentication authentication) {
        String username = getUsernameFromAuthentication(authentication);
        List<Policy> policies = policyService.getPoliciesForUser(username);
        return ResponseEntity.ok(policies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Policy> getPolicyById(@PathVariable Long id, Authentication authentication) {
        String username = getUsernameFromAuthentication(authentication);
        Optional<Policy> policy = policyService.getPolicyByIdForUser(id, username);
        return policy.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Policy>> searchMyPolicies(@RequestParam(required = false) String query, Authentication authentication) {
        String username = getUsernameFromAuthentication(authentication);
        List<Policy> policies = policyService.searchUserPolicies(username, query);
        return ResponseEntity.ok(policies);
    }

    @PostMapping("/quote/auto")
    public ResponseEntity<QuoteResponseDto> getAutoQuote(@Valid @RequestBody AutoQuoteRequestDto requestDto) { return ResponseEntity.ok(quoteService.calculateAutoQuote(requestDto)); }
    @PostMapping("/quote/home")
    public ResponseEntity<QuoteResponseDto> getHomeQuote(@Valid @RequestBody HomeQuoteRequestDto requestDto) { return ResponseEntity.ok(quoteService.calculateHomeQuote(requestDto)); }
    @PostMapping("/quote/health")
    public ResponseEntity<QuoteResponseDto> getHealthQuote(@Valid @RequestBody HealthQuoteRequestDto requestDto) { return ResponseEntity.ok(quoteService.calculateHealthQuote(requestDto)); }
    @PostMapping("/quote/life")
    public ResponseEntity<QuoteResponseDto> getLifeQuote(@Valid @RequestBody LifeQuoteRequestDto requestDto) { return ResponseEntity.ok(quoteService.calculateLifeQuote(requestDto)); }
    @PostMapping("/quote/business")
    public ResponseEntity<QuoteResponseDto> getBusinessQuote(@Valid @RequestBody BusinessQuoteRequestDto requestDto) { return ResponseEntity.ok(quoteService.calculateBusinessQuote(requestDto)); }
    @PostMapping("/quote/travel")
    public ResponseEntity<QuoteResponseDto> getTravelQuote(@Valid @RequestBody TravelQuoteRequestDto requestDto) { return ResponseEntity.ok(quoteService.calculateTravelQuote(requestDto)); }

    private String getUsernameFromAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) { return null; }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) { return ((UserDetails) principal).getUsername(); }
        return principal.toString();
    }

    @PostMapping("/initiate")
    public ResponseEntity<Map<String, String>> initiatePolicyApplication() {
        String pendingId = UUID.randomUUID().toString();
        return ResponseEntity.ok(Map.of("pendingPolicyId", pendingId));
    }

    @PostMapping("/{policyId}/cancel")
    public ResponseEntity<?> requestCancellation(@PathVariable Long policyId, @RequestBody Map<String, String> payload, Authentication authentication) {
        try {
            String username = getUsernameFromAuthentication(authentication);
            String reason = payload.get("reason");
            policyService.requestCancellation(policyId, username, reason);
            return ResponseEntity.ok("Cancellation requested successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}