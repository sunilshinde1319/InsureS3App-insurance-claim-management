package com.insurance.paymentservice.controller;

import com.insurance.paymentservice.dto.PaymentRequestDto;
import com.insurance.paymentservice.service.RazorpayService;
import com.razorpay.Order;
import com.razorpay.RazorpayException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final RazorpayService razorpayService;
    private final String razorpayKeyId;

    public PaymentController(RazorpayService razorpayService, @Value("${razorpay.api.key-id}") String razorpayKeyId) {
        this.razorpayService = razorpayService;
        this.razorpayKeyId = razorpayKeyId;
    }

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@Valid @RequestBody PaymentRequestDto request, Authentication authentication) {
        try {
            String username = authentication.getName();
            Order order = razorpayService.createOrder(request.amount(), "inr", username, request.policyId());


            return ResponseEntity.ok(Map.of(
                    "orderId", order.get("id").toString(),
                    "keyId", razorpayKeyId,
                    "amount", order.get("amount").toString()
            ));
        } catch (RazorpayException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}