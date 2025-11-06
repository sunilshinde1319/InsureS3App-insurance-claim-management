package com.insurance.userservice.dto;

public record UserProfileDto(Long id, String username, String email, String roles) {
}