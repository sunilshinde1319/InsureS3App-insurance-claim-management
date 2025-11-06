package com.insurance.claimservice.service;

import com.insurance.claimservice.client.NotificationClient;
import com.insurance.claimservice.client.UserClient;
import com.insurance.claimservice.dto.ClaimDto;
import com.insurance.claimservice.entity.Claim;
import com.insurance.claimservice.entity.ClaimHistory;
import com.insurance.claimservice.entity.ClaimStatus;
import com.insurance.claimservice.repository.ClaimHistoryRepository;
import com.insurance.claimservice.repository.ClaimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import com.insurance.claimservice.client.PaymentClient;
import com.insurance.claimservice.dto.PayoutRequestDto;

@Service
public class ClaimService {

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private ClaimHistoryRepository claimHistoryRepository;

    @Autowired
    private UserClient userClient;

    @Autowired
    private NotificationClient notificationClient;

    @Autowired
    private PaymentClient paymentClient;

    @Transactional
    public Claim createClaim(ClaimDto claimDto, String username) {
        Claim newClaim = new Claim();
        newClaim.setPolicyId(claimDto.policyId());
        newClaim.setDescription(claimDto.description());
        newClaim.setClaimAmount(claimDto.claimAmount());
        newClaim.setUsername(username);
        newClaim.setClaimNumber(UUID.randomUUID().toString());
        newClaim.setClaimDate(LocalDate.now());
        newClaim.setStatus(ClaimStatus.SUBMITTED);

        // 1. Save the claim first to get a generated ID.
        Claim savedClaim = claimRepository.save(newClaim);

        // 2. Now, create a history entry and explicitly associate it with the saved claim.
        ClaimHistory historyEntry = new ClaimHistory(savedClaim, ClaimStatus.SUBMITTED, "Claim created by user.");
        claimHistoryRepository.save(historyEntry);

        // 3. Return the saved claim. The frontend can then refetch if it needs the history immediately.
        return savedClaim;
    }

    public List<Claim> getClaimsForUser(String username) {
        return claimRepository.findByUsername(username);
    }

    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    @Transactional
    public Optional<Claim> updateClaimStatus(Long claimId, ClaimStatus newStatus) {
        return claimRepository.findById(claimId).map(claim -> {
            if (newStatus == ClaimStatus.APPROVED) {
                claim.setStatus(ClaimStatus.APPROVED_PENDING_PAYOUT);
                claim.addHistory(ClaimStatus.APPROVED_PENDING_PAYOUT, "Claim approved by admin. Payout initiated.");

                // --- THIS IS THE FIX ---
                // Create the DTO with the policyId from the claim object
                PayoutRequestDto payoutRequest = new PayoutRequestDto(
                        claim.getClaimAmount(),
                        claim.getId(),
                        claim.getUsername(),
                        claim.getPolicyId() // <-- ADD THIS LINE
                );
                // --- END OF FIX ---

                paymentClient.initiatePayout(payoutRequest);

            } else {
                claim.setStatus(newStatus);
                claim.addHistory(newStatus, "Status updated by admin.");
            }

            Claim updatedClaim = claimRepository.save(claim);
            sendNotification(updatedClaim);
            return updatedClaim;
        });
    }

    private void sendNotification(Claim claim) {
        String userEmail = userClient.getEmailForUser(claim.getUsername());
        if (userEmail != null) {
            String subject = "Update on your InsureS3App Claim #" + claim.getClaimNumber();
            String body = String.format(
                    "Dear %s,\n\nThis is an update on your claim.\n\n" +
                            "Claim Number: %s\n" +
                            "New Status: %s\n\n" +
                            "You can view the full details by logging into your account.\n\n" +
                            "Thank you,\nThe InsureS3App Team",
                    claim.getUsername(), claim.getClaimNumber(), claim.getStatus()
            );
            notificationClient.sendEmail(userEmail, subject, body);
        }
    }


    public Optional<Claim> getClaimByIdForUser(Long claimId, String username) {
        return claimRepository.findByIdAndUsername(claimId, username);
    }

    public Optional<Claim> getClaimByIdForAdmin(Long claimId) {
        return claimRepository.findById(claimId);
    }

    @Transactional
    public Optional<Claim> addNoteToClaim(Long claimId, String note) {
        Optional<Claim> optionalClaim = claimRepository.findById(claimId);
        if (optionalClaim.isPresent()) {
            Claim claim = optionalClaim.get();
            claim.addHistory(claim.getStatus(), note);
            return Optional.of(claimRepository.save(claim));
        }
        return Optional.empty();
    }

    public List<Claim> searchClaimsByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return claimRepository.findAll();
        }
        return claimRepository.findByUsernameContainingIgnoreCase(username);
    }

    @Transactional
    public void updateClaimStatusFromInternal(Long claimId, ClaimStatus newStatus) {
        claimRepository.findById(claimId).ifPresent(claim -> {
            claim.setStatus(newStatus);
            String note = "Payment status updated: " + newStatus.toString();
            claim.addHistory(newStatus, note);
            claimRepository.save(claim);
        });
    }

    public Map<String, Object> getClaimStatistics() {
        List<Claim> allClaims = claimRepository.findAll();
        long totalClaims = allClaims.size();

        long pendingClaims = allClaims.stream()
                .filter(c -> c.getStatus() == ClaimStatus.SUBMITTED || c.getStatus() == ClaimStatus.IN_REVIEW)
                .count();


        long approvedClaims = allClaims.stream()
                .filter(c -> c.getStatus() == ClaimStatus.APPROVED || c.getStatus() == ClaimStatus.APPROVED_PENDING_PAYOUT || c.getStatus() == ClaimStatus.PAID)
                .count();

        double totalApprovedValue = allClaims.stream()
                .filter(c -> c.getStatus() == ClaimStatus.APPROVED || c.getStatus() == ClaimStatus.APPROVED_PENDING_PAYOUT || c.getStatus() == ClaimStatus.PAID)
                .mapToDouble(Claim::getClaimAmount)
                .sum();

        return Map.of(
                "totalClaims", totalClaims,
                "pendingClaims", pendingClaims,
                "approvedClaims", approvedClaims,
                "totalApprovedValue", totalApprovedValue
        );
    }

}