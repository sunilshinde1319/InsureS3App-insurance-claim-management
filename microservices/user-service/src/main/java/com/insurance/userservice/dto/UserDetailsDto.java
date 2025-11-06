package com.insurance.userservice.dto;

import java.time.LocalDate;

public record UserDetailsDto(
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String address,
        String phoneNumber
) {}