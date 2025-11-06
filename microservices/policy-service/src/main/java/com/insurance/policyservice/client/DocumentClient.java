package com.insurance.policyservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class DocumentClient {
    private static final Logger logger = LoggerFactory.getLogger(DocumentClient.class);
    private final RestTemplate restTemplate;
    private final String documentServiceUrl;

    public DocumentClient(RestTemplate restTemplate, @Value("${document.service.url}") String documentServiceUrl) {
        this.restTemplate = restTemplate;
        this.documentServiceUrl = documentServiceUrl;
    }

    public void finalizeDocumentsForPolicy(String pendingId, Long finalId) {
        String url = documentServiceUrl + "/api/documents/finalize/policy/" + pendingId + "/" + finalId;
        logger.info("Finalizing documents at URL: {}", url);
        try {
            restTemplate.put(url, null);
            logger.info("Successfully finalized documents for pending ID: {}", pendingId);
        } catch (Exception e) {
            logger.error("Failed to finalize documents for pending ID: {}. Error: {}", pendingId, e.getMessage());

        }
    }
}