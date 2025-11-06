package com.insurance.userservice.controller;

import com.insurance.userservice.dto.UserDetailsDto;
import com.insurance.userservice.entity.UserDetails;
import com.insurance.userservice.service.CustomUserDetailsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserDetailsController {

    @Autowired
    private CustomUserDetailsService userService;

    @GetMapping("/details")
    public ResponseEntity<UserDetails> getUserDetails(Authentication authentication) {
        return userService.getUserDetails(authentication.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @RequestMapping(value = "/details", method = {RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<UserDetails> saveOrUpdateUserDetails(Authentication authentication, @Valid @RequestBody UserDetailsDto dto) {
        UserDetails savedDetails = userService.saveUserDetails(authentication.getName(), dto);
        return ResponseEntity.ok(savedDetails);
    }

    @PutMapping("/details/profile-image")
    public ResponseEntity<?> updateUserProfileImage(Authentication authentication, @RequestBody Map<String, String> payload) {
        try {
            String imageUrl = payload.get("imageUrl");
            userService.updateUserProfileImage(authentication.getName(), imageUrl);
            return ResponseEntity.ok("Profile image updated.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}