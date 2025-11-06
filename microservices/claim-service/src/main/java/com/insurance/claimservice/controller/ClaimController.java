package com.insurance.claimservice.controller;

import com.insurance.claimservice.dto.ClaimDto;
import com.insurance.claimservice.entity.Claim;
import com.insurance.claimservice.entity.ClaimStatus;
import com.insurance.claimservice.service.ClaimService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    @Autowired
    private ClaimService claimService;


    @PostMapping
    public ResponseEntity<Claim> createClaim(@Valid @RequestBody ClaimDto claimDto, Authentication authentication) {
        String username = getUsernameFromAuthentication(authentication);
        Claim newClaim = claimService.createClaim(claimDto, username);
        return new ResponseEntity<>(newClaim, HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<List<Claim>> getMyClaims(Authentication authentication) {
        String username = getUsernameFromAuthentication(authentication);
        List<Claim> claims = claimService.getClaimsForUser(username);
        return ResponseEntity.ok(claims);
    }



    /**
     * Endpoint for admins to get all claims.
     * Secured to only allow users with the authority 'ROLE_ADMIN'.
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Claim>> getAllClaims() {
        List<Claim> claims = claimService.getAllClaims();
        return ResponseEntity.ok(claims);
    }

    /**
     * Endpoint for admins to update the status of a claim.
     * Secured to only allow users with the authority 'ROLE_ADMIN'.
     * Expects a JSON body like: { "status": "APPROVED" }
     */
    @PutMapping("/admin/{claimId}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Claim> updateClaimStatus(@PathVariable Long claimId, @RequestBody Map<String, String> statusUpdate) {
        try {
            ClaimStatus newStatus = ClaimStatus.valueOf(statusUpdate.get("status").toUpperCase());
            return claimService.updateClaimStatus(claimId, newStatus)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    private String getUsernameFromAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }


    @GetMapping("/admin/stats")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getClaimStats() {
        Map<String, Object> stats = claimService.getClaimStatistics();
        return ResponseEntity.ok(stats);
    }




    @GetMapping("/{id}")
    public ResponseEntity<Claim> getClaimById(@PathVariable Long id, Authentication authentication) {
        String username = getUsernameFromAuthentication(authentication);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Optional<Claim> claim;
        if (isAdmin) {
            claim = claimService.getClaimByIdForAdmin(id);
        } else {
            claim = claimService.getClaimByIdForUser(id, username);
        }

        return claim.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
    @PostMapping("/admin/{id}/notes")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Claim> addNoteToClaim(@PathVariable Long id, @RequestBody Map<String, String> note) {
        return claimService.addNoteToClaim(id, note.get("note"))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/admin/search")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Claim>> searchClaimsByUsername(@RequestParam String username) {
        List<Claim> claims = claimService.searchClaimsByUsername(username);
        return ResponseEntity.ok(claims);
    }

}