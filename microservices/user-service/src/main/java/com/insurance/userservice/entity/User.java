package com.insurance.userservice.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    private String roles = "USER";

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private UserDetails userDetails;

    private String passwordResetOtp;
    private LocalDateTime passwordResetOtpExpiry;

    // Constructors
    public User() {}
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRoles() { return roles; }
    public void setRoles(String roles) { this.roles = roles; }
    public UserDetails getUserDetails() { return userDetails; }
    public void setUserDetails(UserDetails userDetails) { this.userDetails = userDetails; }

    public String getPasswordResetOtp() { return passwordResetOtp; }
    public void setPasswordResetOtp(String passwordResetOtp) { this.passwordResetOtp = passwordResetOtp; }
    public LocalDateTime getPasswordResetOtpExpiry() { return passwordResetOtpExpiry; }
    public void setPasswordResetOtpExpiry(LocalDateTime passwordResetOtpExpiry) { this.passwordResetOtpExpiry = passwordResetOtpExpiry; }
}