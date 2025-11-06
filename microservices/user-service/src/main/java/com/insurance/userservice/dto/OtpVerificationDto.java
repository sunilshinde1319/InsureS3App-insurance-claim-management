package com.insurance.userservice.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record OtpVerificationDto(
        @NotBlank String otp,
        @NotBlank @Size(min = 8, message = "Password must be at least 8 characters long") String newPassword
) {}