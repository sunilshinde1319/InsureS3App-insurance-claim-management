package com.insurance.policyservice.controller;

import com.insurance.policyservice.dto.PolicyTypeDto;
import com.insurance.policyservice.entity.PolicyType;
import com.insurance.policyservice.service.PolicyTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/policy-types")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class PolicyTypeAdminController {

    @Autowired private PolicyTypeService policyTypeService;

    @GetMapping
    public ResponseEntity<List<PolicyType>> getAllPolicyTypes() {
        return ResponseEntity.ok(policyTypeService.getAllPolicyTypes());
    }

    @PostMapping
    public ResponseEntity<?> createPolicyType(@Valid @RequestBody PolicyTypeDto dto) {
        try {
            return new ResponseEntity<>(policyTypeService.createPolicyType(dto), HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PolicyType> updatePolicyType(@PathVariable Long id, @Valid @RequestBody PolicyTypeDto dto) {
        return ResponseEntity.ok(policyTypeService.updatePolicyType(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePolicyType(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String reason = payload.get("reason");
        if (reason == null || reason.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("A reason is required for deletion.");
        }
        policyTypeService.deletePolicyType(id, reason);
        return ResponseEntity.ok("Policy type and associated plans deleted successfully.");
    }
}