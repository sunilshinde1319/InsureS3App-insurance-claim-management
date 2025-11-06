package com.insurance.userservice.controller;

import com.insurance.userservice.dto.*;
import com.insurance.userservice.entity.User;
import com.insurance.userservice.repository.UserRepository;
import com.insurance.userservice.security.JwtUtil;
import com.insurance.userservice.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDto userDto) {

        if (userRepository.findByUsername(userDto.username()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("username", "This username is already taken."));
        }

        if (userRepository.findByEmail(userDto.email()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("email", "This email address is already in use."));
        }

        User user = new User();
        user.setUsername(userDto.username());
        user.setEmail(userDto.email());
        user.setPassword(passwordEncoder.encode(userDto.password()));
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
        // Authenticate the user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.username());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(jwt));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getUserProfile(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .map(user -> new UserProfileDto(user.getId(), user.getUsername(), user.getEmail(), user.getRoles()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(Authentication authentication, @Valid @RequestBody UserProfileUpdateDto updateDto) {
        try {
            customUserDetailsService.updateUserProfile(authentication.getName(), updateDto);
            return ResponseEntity.ok("Profile updated successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/profile/change-password")
    public ResponseEntity<?> changePassword(Authentication authentication, @Valid @RequestBody PasswordChangeDto passwordDto) {
        try {

            customUserDetailsService.changeUserPassword(authentication.getName(), passwordDto);

            return ResponseEntity.ok("Password changed successfully.");
        } catch (IllegalStateException e) {
            if (e.getMessage().contains("Incorrect current password")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}