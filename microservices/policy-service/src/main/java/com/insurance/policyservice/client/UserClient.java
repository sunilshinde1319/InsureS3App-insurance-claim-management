package com.insurance.policyservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class UserClient {
    private static final Logger logger = LoggerFactory.getLogger(UserClient.class);
    private final RestTemplate restTemplate;
    private final String userServiceUrl;

    public UserClient(RestTemplate restTemplate, @Value("${user.service.url}") String userServiceUrl) {
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
    }

    public String getEmailForUser(String username) {
        String url = userServiceUrl + "/api/internal/users/by-username/" + username;
        logger.info("Attempting to get user details from URL: {}", url);
        try {
            @SuppressWarnings("unchecked")
            Map<String, String> userDetails = restTemplate.getForObject(url, Map.class);

            if (userDetails != null) {
                logger.info("Successfully retrieved details for user: {}", username);
                return userDetails.get("email");
            } else {
                logger.warn("Received null response for user: {}", username);
                return null;
            }
        } catch (Exception e) {
            logger.error("Failed to get user details for user: {}. Error: {}", username, e.getMessage());
            return null;
        }
    }
}