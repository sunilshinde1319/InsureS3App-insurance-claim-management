package com.insurance.userservice.controller;

import com.insurance.userservice.dto.OtpVerificationDto;
import com.insurance.userservice.dto.PasswordResetRequestDto;
import com.insurance.userservice.service.CustomUserDetailsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import com.insurance.userservice.dto.OtpValidationRequestDto;

@RestController
@RequestMapping("/api/public/password-reset")
public class PasswordResetController {

    @Autowired
    private CustomUserDetailsService userService;

    @PostMapping("/request")
    public ResponseEntity<String> requestPasswordReset(@Valid @RequestBody PasswordResetRequestDto requestDto) {
        try {
            userService.generatePasswordResetOtp(requestDto.email());
            return ResponseEntity.ok("An OTP has been sent to your email address.");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.ok("An OTP has been sent to your email address.");
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtpAndReset(@Valid @RequestBody OtpVerificationDto verificationDto) {
        try {
            userService.verifyOtpAndResetPassword(verificationDto.otp(), verificationDto.newPassword());
            return ResponseEntity.ok("Password has been reset successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<String> validateOtp(@Valid @RequestBody OtpValidationRequestDto validationRequestDto) {
        try {
            userService.validateOtp(validationRequestDto.otp());
            return ResponseEntity.ok("OTP is valid.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}