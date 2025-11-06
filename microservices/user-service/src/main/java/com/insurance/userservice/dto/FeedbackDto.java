package com.insurance.userservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record FeedbackDto(
        @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating cannot be more than 5")
        Integer rating,

        @NotBlank(message = "Tag cannot be blank")
        String tag,

        @NotBlank(message = "Feedback text cannot be blank")
        String text
) {
}