package com.insurance.claimservice.controller;

import com.insurance.claimservice.entity.ClaimStatus;
import com.insurance.claimservice.service.ClaimService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/internal/claims")
public class InternalClaimController {

    private final ClaimService claimService;

    public InternalClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    // This endpoint will be called by the payment-service to update the status
    @PutMapping("/{claimId}/status")
    public ResponseEntity<String> updateClaimStatusFromPayment(@PathVariable Long claimId, @RequestBody Map<String, String> payload) {
        try {
            String status = payload.get("status");
            ClaimStatus newStatus = ClaimStatus.valueOf(status.toUpperCase());

            claimService.updateClaimStatusFromInternal(claimId, newStatus);
            return ResponseEntity.ok("Claim status updated successfully.");
        } catch (Exception e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}