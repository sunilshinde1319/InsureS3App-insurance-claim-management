package com.insurance.policyservice.controller;

import com.insurance.policyservice.entity.Policy;
import com.insurance.policyservice.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;


import java.util.List;
@RestController
@RequestMapping("/api/internal/policies")
public class InternalPolicyController {

    @Autowired
    private PolicyService policyService;

    @GetMapping("/user/{username}")
    public ResponseEntity<List<Policy>> getPoliciesForUser(@PathVariable String username) {
        return ResponseEntity.ok(policyService.getPoliciesForUser(username));
    }


    @PutMapping("/{policyId}/activate")
    public ResponseEntity<Void> activatePolicy(@PathVariable Long policyId) {
        try {
            policyService.activatePolicy(policyId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }


    }

}