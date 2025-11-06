package com.insurance.userservice.controller;

import com.insurance.userservice.dto.UserProfileDto;
import com.insurance.userservice.entity.User;

import com.insurance.userservice.repository.UserRepository;
import com.insurance.userservice.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class UserAdminController {

    @Autowired
    private CustomUserDetailsService userService;
    @Autowired
    private UserRepository userRepository;


    /**
     * Endpoint for admins to get all users.
     * Converts User entities to UserProfileDto to avoid sending passwords.
     */
    @GetMapping("/all")
    public ResponseEntity<List<UserProfileDto>> getAllUsers() {
        List<UserProfileDto> userProfiles = userService.getAllUsers().stream()
                .map(user -> new UserProfileDto(user.getId(), user.getUsername(), user.getEmail(), user.getRoles()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(userProfiles);
    }

    /**
     * Endpoint for admins to update a user's roles.
     * Expects a JSON body like: { "roles": "USER,ADMIN" }
     */
    @PutMapping("/{userId}/roles")
    public ResponseEntity<?> updateUserRoles(@PathVariable Long userId, @RequestBody Map<String, String> roleUpdate) {
        try {
            String newRoles = roleUpdate.get("roles");
            userService.updateUserRoles(userId, newRoles);
            return ResponseEntity.ok("User roles updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUserByAdmin(@PathVariable Long userId, @RequestBody Map<String, String> payload) {
        String reason = payload.get("reason");
        if (reason == null || reason.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("A reason for deletion is required.");
        }

        try {
            userService.deleteUserByAdmin(userId, reason);
            return ResponseEntity.ok("User account deleted successfully.");
        } catch (IllegalStateException e) {
            // This will catch the "active policies" error
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserProfileDto>> searchUsers(@RequestParam String query) {
        List<UserProfileDto> userProfiles = userRepository.findByUsernameContainingIgnoreCase(query).stream()
                .map(user -> new UserProfileDto(user.getId(), user.getUsername(), user.getEmail(), user.getRoles()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(userProfiles);
    }

}