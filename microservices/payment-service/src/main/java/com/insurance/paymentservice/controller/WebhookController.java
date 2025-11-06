package com.insurance.paymentservice.controller;

import com.insurance.paymentservice.service.RazorpayService;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments/razorpay")
public class WebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

    private final RazorpayService razorpayService;
    private final String webhookSecret;

    public WebhookController(RazorpayService razorpayService, @Value("${razorpay.webhook.secret}") String webhookSecret) {
        this.razorpayService = razorpayService;
        this.webhookSecret = webhookSecret;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleRazorpayWebhook(@RequestBody String payload, @RequestHeader("X-Razorpay-Signature") String signature) {
        logger.info("================= WEBHOOK RECEIVED ==================");
        logger.info("Payload: {}", payload);
        logger.info("Signature: {}", signature);

        try {
            // --- THIS IS THE CRITICAL DEBUGGING STEP ---
            // We will now verify the signature and log the outcome explicitly.
            boolean isValid = Utils.verifyWebhookSignature(payload, signature, webhookSecret);

            if (!isValid) {
                logger.error("!!! SIGNATURE VERIFICATION FAILED !!! The request may not be from Razorpay.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
            }

            logger.info("--- SIGNATURE VERIFICATION SUCCEEDED ---");

        } catch (Exception e) {

            logger.error("!!! An exception occurred during signature verification !!!", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Signature verification failed with an exception.");
        }

        try {
            JSONObject payloadJson = new JSONObject(payload);
            String event = payloadJson.getString("event");
            logger.info("Processing event: {}", event);

            if ("payment.captured".equals(event)) {
                JSONObject paymentEntity = payloadJson.getJSONObject("payload").getJSONObject("payment").getJSONObject("entity");
                if (paymentEntity.has("order_id") && !paymentEntity.isNull("order_id")) {
                    String orderId = paymentEntity.getString("order_id");
                    logger.info("Event is 'payment.captured'. Calling service for Order ID: {}", orderId);
                    razorpayService.handleSuccessfulPayment(orderId);
                } else {
                    logger.warn("Webhook 'payment.captured' received but 'order_id' was missing or null.");
                }
            } else {
                logger.info("Ignoring event '{}' as it is not 'payment.captured'.", event);
            }

        } catch (Exception e) {
            logger.error("Error processing the webhook JSON payload", e);
        }

        logger.info("================= WEBHOOK PROCESSING END ==================");
        return ResponseEntity.ok("Received");
    }
}