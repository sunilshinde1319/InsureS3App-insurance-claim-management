package com.insurance.claimservice.client;

import com.insurance.claimservice.dto.PayoutRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PaymentClient {
    private static final Logger logger = LoggerFactory.getLogger(PaymentClient.class);
    private final RestTemplate restTemplate;
    private final String paymentServiceUrl;

    public PaymentClient(RestTemplate restTemplate, @Value("${payment.service.url}") String paymentServiceUrl) {
        this.restTemplate = restTemplate;
        this.paymentServiceUrl = paymentServiceUrl;
    }

    public void initiatePayout(PayoutRequestDto payoutRequest) {
        String url = paymentServiceUrl + "/api/internal/payments/payout";
        try {
            restTemplate.postForObject(url, payoutRequest, String.class);
            logger.info("Successfully sent payout request for claim ID: {}", payoutRequest.claimId());
        } catch (Exception e) {
            logger.error("Failed to send payout request for claim ID: {}. Error: {}", payoutRequest.claimId(), e.getMessage());
            throw new RuntimeException("Could not initiate payout", e);
        }
    }
}