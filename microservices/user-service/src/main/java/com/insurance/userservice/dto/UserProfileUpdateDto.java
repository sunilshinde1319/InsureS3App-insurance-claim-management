package com.insurance.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserProfileUpdateDto(
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email should be valid")
        String email
) {
}