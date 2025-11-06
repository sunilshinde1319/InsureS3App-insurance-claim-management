package com.insurance.policyservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class NotificationClient {
    private static final Logger logger = LoggerFactory.getLogger(NotificationClient.class); // Correct logger
    private final RestTemplate restTemplate;
    private final String notificationServiceUrl;


    public NotificationClient(RestTemplate restTemplate, @Value("${notification.service.url}") String notificationServiceUrl) {
        this.restTemplate = restTemplate;
        this.notificationServiceUrl = notificationServiceUrl;
    }


    public void sendEmail(String to, String subject, String body) {
        String url = notificationServiceUrl + "/api/notifications/send";
        Map<String, String> request = Map.of("to", to, "subject", subject, "body", body);
        logger.info("Sending notification request to URL: {}", url);
        try {
            restTemplate.postForObject(url, request, String.class);
            logger.info("Successfully sent notification request for user: {}", to);
        } catch (Exception e) {
            logger.error("Failed to send notification request for user: {}. Error: {}", to, e.getMessage());
        }
    }
}