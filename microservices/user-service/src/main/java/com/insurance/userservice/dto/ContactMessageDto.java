package com.insurance.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record ContactMessageDto(
        @NotBlank(message = "Name cannot be blank")
        String name,

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Please provide a valid email address")
        String email,

        @NotBlank(message = "Subject cannot be blank")
        String subject,

        @NotBlank(message = "Message cannot be blank")
        @Size(min = 10, message = "Message must be at least 10 characters long")
        String message
) {
}