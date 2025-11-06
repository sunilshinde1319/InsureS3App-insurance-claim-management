package com.insurance.userservice.dto;

import jakarta.validation.constraints.NotBlank;

public record OtpValidationRequestDto(
        @NotBlank(message = "OTP cannot be blank")
        String otp
) {}