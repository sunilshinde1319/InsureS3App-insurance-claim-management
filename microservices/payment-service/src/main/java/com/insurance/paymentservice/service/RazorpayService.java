package com.insurance.paymentservice.service;

import com.insurance.paymentservice.entity.Payment;
import com.insurance.paymentservice.entity.PaymentStatus;
import com.insurance.paymentservice.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import jakarta.annotation.PostConstruct;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class RazorpayService {


    private static final Logger logger = LoggerFactory.getLogger(RazorpayService.class);


    @Value("${razorpay.api.key-id}")
    private String keyId;

    @Value("${razorpay.api.key-secret}")
    private String keySecret;

    @Value("${claim.service.url}")
    private String claimServiceUrl;

    @Value("${policy.service.url}")
    private String policyServiceUrl;

    private RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public RazorpayService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @PostConstruct
    public void init() throws RazorpayException {
        this.razorpayClient = new RazorpayClient(keyId, keySecret);
    }

    @Transactional
    public Order createOrder(Double amount, String currency, String username, Long policyId) throws RazorpayException {
        logger.info("Creating Razorpay order for policy ID: {}", policyId);
        Payment payment = new Payment();
        payment.setAmount(amount);
        payment.setCurrency(currency.toUpperCase());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setUsername(username);
        payment.setPolicyId(policyId);
        payment = paymentRepository.save(payment);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int) (amount * 100));
        orderRequest.put("currency", currency.toUpperCase());
        orderRequest.put("receipt", "receipt_policy_" + payment.getId());

        Order order = razorpayClient.orders.create(orderRequest);

        String razorpayOrderId = order.get("id");
        payment.setGatewayOrderId(razorpayOrderId);
        paymentRepository.save(payment);
        logger.info("Successfully created Razorpay order {} for policy ID: {}", razorpayOrderId, policyId);
        return order;
    }

    @Transactional
    public void handleSuccessfulPayment(String razorpayOrderId) {
        Payment payment = paymentRepository.findByGatewayOrderId(razorpayOrderId)
                .orElseThrow(() -> new IllegalStateException("Payment not found for Razorpay Order ID: " + razorpayOrderId));

        if (payment.getStatus() == PaymentStatus.PENDING) {
            payment.setStatus(PaymentStatus.SUCCEEDED);
            paymentRepository.save(payment);
            logger.info("Payment status updated to SUCCEEDED for Order ID: {}", razorpayOrderId);

            if (payment.getPolicyId() != null) {
                activatePolicy(payment.getPolicyId());
            }
        } else {
            logger.warn("Received webhook for an already processed Order ID: {}", razorpayOrderId);
        }
    }

    @Transactional
    public void initiateClaimPayout(Long claimId, Double amount, String username, Long policyId) { // <-- ADD policyId here
        logger.info("Initiating payout process for Claim ID: {}", claimId);

        Payment payoutRecord = new Payment();
        payoutRecord.setAmount(amount);
        payoutRecord.setCurrency("INR");
        payoutRecord.setStatus(PaymentStatus.PAYOUT_PROCESSING);
        payoutRecord.setUsername(username);
        payoutRecord.setClaimId(claimId);
        payoutRecord.setPolicyId(policyId);

        paymentRepository.save(payoutRecord);

        updateClaimStatus(claimId, "APPROVED_PENDING_PAYOUT");

        logger.info("Payout for Claim ID: {} is now processing.", claimId);
    }


    private void activatePolicy(Long policyId) {
        String url = policyServiceUrl + "/api/internal/policies/" + policyId + "/activate";
        try {
            restTemplate.put(url, null);
            logger.info("Successfully sent activation request for Policy ID: {}", policyId);
        } catch (Exception e) {
            logger.error("Failed to send activation request for Policy ID: {}. Error: {}", policyId, e.getMessage());
        }
    }

    private void updateClaimStatus(Long claimId, String status) {
        String url = claimServiceUrl + "/api/internal/claims/" + claimId + "/status";
        try {
            restTemplate.put(url, Map.of("status", status));
            logger.info("Successfully sent status update '{}' for Claim ID: {}", status, claimId);
        } catch (Exception e) {
            logger.error("Failed to send status update for Claim ID: {}. Error: {}", claimId, e.getMessage());
        }
    }
}