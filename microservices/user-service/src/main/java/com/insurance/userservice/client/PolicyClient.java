package com.insurance.userservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Component
public class PolicyClient {
    private final RestTemplate restTemplate;
    private final String policyServiceUrl;

    public PolicyClient(RestTemplate restTemplate, @Value("${policy.service.url}") String policyServiceUrl) {
        this.restTemplate = restTemplate;
        this.policyServiceUrl = policyServiceUrl;
    }

    public List<Map<String, Object>> getPoliciesForUser(String username) {
        try {
            String url = policyServiceUrl + "/api/internal/policies/user/" + username;
            return restTemplate.getForObject(url, List.class);
        } catch (Exception e) {
            return List.of();
        }
    }
}