package com.insurance.notificationservice.dto;


public record EmailRequestDto(String to, String subject, String body) {
}