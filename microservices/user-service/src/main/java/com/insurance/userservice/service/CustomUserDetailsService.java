package com.insurance.userservice.service;

import com.insurance.userservice.client.PolicyClient;
import com.insurance.userservice.dto.PasswordChangeDto;
import com.insurance.userservice.dto.UserDetailsDto;
import com.insurance.userservice.dto.UserProfileUpdateDto;
import com.insurance.userservice.entity.User;
import com.insurance.userservice.repository.UserDetailsRepository;
import com.insurance.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.insurance.userservice.client.NotificationClient;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final PolicyClient policyClient;
    private final NotificationClient notificationClient;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository,
                                    UserDetailsRepository userDetailsRepository,
                                    PolicyClient policyClient,
                                    NotificationClient notificationClient) {
        this.userRepository = userRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.policyClient = policyClient;
        this.notificationClient = notificationClient;
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        List<GrantedAuthority> authorities = Arrays.stream(user.getRoles().split(","))
                .map(role -> "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());


        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    @Transactional
    public User updateUserProfile(String currentUsername, UserProfileUpdateDto updateDto) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Optional<User> userByNewEmail = userRepository.findByEmail(updateDto.email());
        if (userByNewEmail.isPresent() && !userByNewEmail.get().getId().equals(user.getId())) {
            throw new IllegalStateException("Email is already in use.");
        }

        user.setEmail(updateDto.email());
        return userRepository.save(user);
    }

    @Transactional
    public void changeUserPassword(String username, PasswordChangeDto passwordDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!this.passwordEncoder.matches(passwordDto.currentPassword(), user.getPassword())) {
            throw new IllegalStateException("Incorrect current password.");
        }

        user.setPassword(this.passwordEncoder.encode(passwordDto.newPassword()));
        userRepository.save(user);

        sendPasswordChangeConfirmationEmail(user);
    }

    private void sendPasswordChangeConfirmationEmail(User user) {
        String subject = "Security Alert: Your InsureS3App Password Has Been Changed";
        String body = String.format(
                "Dear %s,\n\n" +
                        "This is a confirmation that the password for your InsureS3App account was successfully changed from your profile settings.\n\n" +
                        "If you did not make this change, please contact our support team immediately to secure your account.\n\n" +
                        "Thank you,\n" +
                        "The InsureS3App Team",
                user.getUsername()
        );
        notificationClient.sendEmail(user.getEmail(), subject, body);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateUserRoles(Long userId, String newRoles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        if (newRoles == null || newRoles.trim().isEmpty()) {
            throw new IllegalArgumentException("Roles cannot be empty.");
        }

        user.setRoles(newRoles);
        return userRepository.save(user);
    }


    public Optional<com.insurance.userservice.entity.UserDetails> getUserDetails(String username) {
        return userRepository.findByUsername(username).map(User::getUserDetails);
    }


    @Transactional
    public com.insurance.userservice.entity.UserDetails saveUserDetails(String username, UserDetailsDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        com.insurance.userservice.entity.UserDetails details = user.getUserDetails();
        if (details == null) {
            details = new com.insurance.userservice.entity.UserDetails();
            details.setUser(user);
            user.setUserDetails(details);
        }

        details.setFirstName(dto.firstName());
        details.setLastName(dto.lastName());
        details.setDateOfBirth(dto.dateOfBirth());
        details.setAddress(dto.address());
        details.setPhoneNumber(dto.phoneNumber());


        User savedUser = userRepository.save(user);

        return savedUser.getUserDetails();
    }

    @Transactional
    public com.insurance.userservice.entity.UserDetails updateUserProfileImage(String username, String imageUrl) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        com.insurance.userservice.entity.UserDetails details = user.getUserDetails();

        if (details == null) {
            details = new com.insurance.userservice.entity.UserDetails();
            details.setUser(user);
            user.setUserDetails(details);
        }

        details.setProfileImageUrl(imageUrl);

        return userDetailsRepository.save(details);
    }

    public Map<String, Object> checkDeletionStatus(String username) {
        List<Map<String, Object>> policies = policyClient.getPoliciesForUser(username);
        List<String> activePolicyNumbers = policies.stream()
                .filter(p -> "ACTIVE".equals(p.get("status")) || "PENDING_APPROVAL".equals(p.get("status")))
                .map(p -> (String) p.get("policyNumber"))
                .collect(Collectors.toList());

        if (!activePolicyNumbers.isEmpty()) {
            return Map.of("status", "BLOCKED", "activePolicies", activePolicyNumbers);
        }
        return Map.of("status", "ALLOWED");
    }

    @Transactional
    public void deleteUser(String username, String password, PasswordEncoder passwordEncoder) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Use the passed-in encoder
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalStateException("Incorrect password.");
        }

        Map<String, Object> deletionStatus = this.checkDeletionStatus(username);
        if ("BLOCKED".equals(deletionStatus.get("status"))) {
            throw new IllegalStateException("Cannot delete account: User still has active policies.");
        }

        String userEmail = user.getEmail();

        userRepository.delete(user);

        if (userEmail != null) {
            String subject = "Your InsureS3App Account Has Been Deleted";
            String body = String.format(
                    "Dear %s,\n\nThis is a confirmation that your account with InsureS3App has been permanently deleted as requested.\n\n" +
                            "If you did not request this, please contact our support team immediately.\n\n" +
                            "Thank you for being a customer,\nThe InsureS3App Team",
                    username
            );
            notificationClient.sendEmail(userEmail, subject, body);
        }
    }


    @Transactional
    public void deleteUserByAdmin(Long userId, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        Map<String, Object> deletionStatus = this.checkDeletionStatus(user.getUsername());
        if ("BLOCKED".equals(deletionStatus.get("status"))) {
            throw new IllegalStateException("Cannot delete user. All active or pending policies must be closed first.");
        }

        String userEmail = user.getEmail();
        String username = user.getUsername();

        userRepository.delete(user);

        if (userEmail != null) {
            String subject = "Your InsureS3App Account Has Been Deleted by an Administrator";
            String body = String.format(
                    "Dear %s,\n\nThis is a confirmation that your account with InsureS3App has been permanently deleted by an administrator.\n\n" +
                            "Reason provided: %s\n\n" +
                            "If you believe this was in error, please contact our support team immediately.\n\n" +
                            "The InsureS3App Team",
                    username,
                    reason
            );
            notificationClient.sendEmail(userEmail, subject, body);
        }
    }

    @Transactional
    public void generatePasswordResetOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with this email address."));

        String otp = String.format("%06d", new Random().nextInt(999999));

        user.setPasswordResetOtp(otp);
        user.setPasswordResetOtpExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        String subject = "Your Password Reset OTP for InsureS3App";
        String body = String.format(
                "Dear %s,\n\nYour One-Time Password (OTP) to reset your password is: %s\n\n" +
                        "This OTP is valid for 10 minutes. If you did not request this, please ignore this email.\n\n" +
                        "Thank you,\nThe InsureS3App Team",
                user.getUsername(), otp
        );
        notificationClient.sendEmail(user.getEmail(), subject, body);
    }

    @Transactional
    public void verifyOtpAndResetPassword(String otp, String newPassword) {
        User user = userRepository.findByPasswordResetOtp(otp)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired OTP."));

        if (user.getPasswordResetOtpExpiry().isBefore(LocalDateTime.now())) {
            user.setPasswordResetOtp(null);
            user.setPasswordResetOtpExpiry(null);
            userRepository.save(user);
            throw new IllegalArgumentException("Invalid or expired OTP.");
        }

        user.setPassword(this.passwordEncoder.encode(newPassword));
        user.setPasswordResetOtp(null);
        user.setPasswordResetOtpExpiry(null);
        userRepository.save(user);

        sendPasswordResetSuccessNotification(user);
    }

    private void sendPasswordResetSuccessNotification(User user) {
        String subject = "Your Password Has Been Changed";
        String body = String.format(
                "Dear %s,\n\nThis is a confirmation that the password for your InsureS3App account has been successfully changed.\n\n" +
                        "If you did not make this change, please contact our support team immediately.\n\n" +
                        "Thank you,\nThe InsureS3App Team",
                user.getUsername()
        );
        notificationClient.sendEmail(user.getEmail(), subject, body);
    }

    public void validateOtp(String otp) {
        User user = userRepository.findByPasswordResetOtp(otp)
                .orElseThrow(() -> new IllegalArgumentException("Invalid OTP."));

        if (user.getPasswordResetOtpExpiry().isBefore(LocalDateTime.now())) {
            user.setPasswordResetOtp(null);
            user.setPasswordResetOtpExpiry(null);
            userRepository.save(user);
            throw new IllegalArgumentException("OTP has expired.");
        }
    }

}