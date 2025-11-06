package com.insurance.policyservice.controller;

import com.insurance.policyservice.entity.CancellationRequest;
import com.insurance.policyservice.entity.Policy;
import com.insurance.policyservice.entity.PolicyStatus;
import com.insurance.policyservice.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/policies/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class PolicyAdminController {

    @Autowired
    private PolicyService policyService;

    /**
     * Endpoint for admins to get a list of ALL policies in the system.
     */
    @GetMapping("/all")
    public ResponseEntity<List<Policy>> getAllPolicies() {
        List<Policy> policies = policyService.getAllPolicies();
        return ResponseEntity.ok(policies);
    }

    /**
     * Endpoint for admins to update the status of any policy.
     */
    @PutMapping("/{policyId}/status")
    public ResponseEntity<?> updatePolicyStatus(@PathVariable Long policyId, @RequestBody Map<String, String> statusUpdate) {
        try {
            PolicyStatus newStatus = PolicyStatus.valueOf(statusUpdate.get("status").toUpperCase());
            return policyService.updatePolicyStatus(policyId, newStatus)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status value.");
        }
    }


    @GetMapping("/{policyId}")
    public ResponseEntity<Policy> getPolicyByIdForAdmin(@PathVariable Long policyId) {
        return policyService.getPolicyByIdForAdmin(policyId) // We need to create this service method
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cancellations")
    public ResponseEntity<List<CancellationRequest>> getPendingCancellations() {
        return ResponseEntity.ok(policyService.getPendingCancellations());
    }

    @PutMapping("/cancel/{requestId}/approve")
    public ResponseEntity<?> approveCancellation(@PathVariable Long requestId) {
        try {
            policyService.approveCancellation(requestId);
            return ResponseEntity.ok("Cancellation approved successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{policyId}")
    public ResponseEntity<?> closePolicyByAdmin(@PathVariable Long policyId, @RequestBody Map<String, String> payload) {
        String reason = payload.get("reason");
        if (reason == null || reason.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("A reason is required to close the policy.");
        }
        try {
            policyService.closePolicyByAdmin(policyId, reason);
            return ResponseEntity.ok("Policy closed successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getPolicyStats() {
        return ResponseEntity.ok(policyService.getAdminDashboardStats());
    }


}