package com.insurance.userservice.controller;

import com.insurance.userservice.dto.UserProfileDto;
import com.insurance.userservice.entity.User; // Make sure to import your User entity
import com.insurance.userservice.entity.UserDetails;
import com.insurance.userservice.repository.UserRepository;
import com.insurance.userservice.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/users")
public class InternalController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService userService;

    /**
     * This endpoint combines the basic profile and detailed profile into one.
     * It fetches the User entity, which contains all the necessary information.
     */
    @GetMapping("/by-username/{username}")
    public ResponseEntity<User> getFullUserDetailsByUsername(@PathVariable String username) {
        return userRepository.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}