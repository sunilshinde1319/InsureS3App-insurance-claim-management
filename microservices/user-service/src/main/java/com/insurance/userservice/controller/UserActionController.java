package com.insurance.userservice.controller;

import com.insurance.userservice.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder; // <-- NEW IMPORT
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserActionController {

    @Autowired
    private CustomUserDetailsService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/check-deletion-status")
    public ResponseEntity<Map<String, Object>> checkDeletionStatus(Authentication authentication) {
        String username = authentication.getName();
        Map<String, Object> status = userService.checkDeletionStatus(username);
        return ResponseEntity.ok(status);
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<String> deleteAccount(Authentication authentication, @RequestBody Map<String, String> payload) {
        try {
            String username = authentication.getName();
            String password = payload.get("password");

            if (password == null || password.isEmpty()) {
                return ResponseEntity.badRequest().body("Password is required to confirm deletion.");
            }


            userService.deleteUser(username, password, passwordEncoder);

            return ResponseEntity.ok("Your account has been permanently deleted.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while deleting your account.");
        }
    }
}