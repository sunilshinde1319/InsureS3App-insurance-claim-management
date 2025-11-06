package com.insurance.paymentservice.controller;

import com.insurance.paymentservice.dto.PayoutRequestDto;
import com.insurance.paymentservice.service.RazorpayService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/internal/payments")
public class InternalPaymentController {

    private final RazorpayService razorpayService;


    public InternalPaymentController(RazorpayService razorpayService) {
        this.razorpayService = razorpayService;
    }


    @PostMapping("/payout")
    public ResponseEntity<String> initiatePayout(@Valid @RequestBody PayoutRequestDto request) {
        razorpayService.initiateClaimPayout(request.claimId(), request.amount(), request.username(), request.policyId());
        return ResponseEntity.ok("Payout process initiated for claim " + request.claimId());
    }
}