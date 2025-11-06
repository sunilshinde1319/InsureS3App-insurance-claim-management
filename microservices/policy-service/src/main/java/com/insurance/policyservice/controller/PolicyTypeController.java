package com.insurance.policyservice.controller;

import com.insurance.policyservice.entity.PolicyType;
import com.insurance.policyservice.service.PolicyTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/policy-types")
public class PolicyTypeController {

    @Autowired
    private PolicyTypeService policyTypeService;

    @GetMapping
    public ResponseEntity<List<PolicyType>> getEnabledPolicyTypes() {
        return ResponseEntity.ok(policyTypeService.getEnabledPolicyTypes());
    }
}