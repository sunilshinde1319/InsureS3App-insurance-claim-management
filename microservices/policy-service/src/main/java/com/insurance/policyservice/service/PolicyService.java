package com.insurance.policyservice.service;

import com.insurance.policyservice.client.NotificationClient;
import com.insurance.policyservice.client.UserClient;
import com.insurance.policyservice.dto.PolicyDto;
import com.insurance.policyservice.entity.CancellationRequest;
import com.insurance.policyservice.entity.Policy;
import com.insurance.policyservice.entity.PolicyStatus;
import com.insurance.policyservice.repository.CancellationRequestRepository;
import com.insurance.policyservice.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.insurance.policyservice.client.DocumentClient;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PolicyService {

    @Autowired
    private PolicyRepository policyRepository;
    @Autowired
    private UserClient userClient;
    @Autowired
    private NotificationClient notificationClient;

    @Autowired
    private DocumentClient documentClient;

    @Autowired
    private CancellationRequestRepository cancellationRequestRepository;

    @Transactional
    public Policy createPolicy(PolicyDto policyDto, String username, String pendingPolicyId)  {
        Policy newPolicy = new Policy();
        newPolicy.setPolicyType(policyDto.policyType());
        newPolicy.setCoverageAmount(policyDto.coverageAmount());
        newPolicy.setPremium(policyDto.premium());
        newPolicy.setUsername(username);
        String shortUUID = UUID.randomUUID().toString().split("-")[0];
        newPolicy.setPolicyNumber("POL-" + shortUUID);
        newPolicy.setStartDate(LocalDate.now());
        newPolicy.setEndDate(LocalDate.now().plusYears(1));
        newPolicy.setStatus(PolicyStatus.PENDING_APPROVAL);

        Policy savedPolicy = policyRepository.save(newPolicy);


        if (pendingPolicyId != null && !pendingPolicyId.isEmpty()) {
            documentClient.finalizeDocumentsForPolicy(pendingPolicyId, savedPolicy.getId());
        }

        return savedPolicy;
    }

    public List<Policy> getPoliciesForUser(String username) {
        return policyRepository.findByUsername(username);
    }


    public Optional<Policy> getPolicyByIdForUser(Long policyId, String username) {
        return policyRepository.findByIdAndUsername(policyId, username);
    }
    public Optional<Policy> getPolicyByIdForAdmin(Long policyId) {
        return policyRepository.findById(policyId);
    }
    public List<Policy> searchUserPolicies(String username, String query) {
        if (query == null || query.trim().isEmpty()) {
            return policyRepository.findByUsername(username);
        }
        return policyRepository.searchByUsernameAndQuery(username, query);
    }

    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }

    @Transactional
    public Optional<Policy> updatePolicyStatus(Long policyId, PolicyStatus newStatus) {
        return policyRepository.findById(policyId).map(policy -> {


            if (newStatus == PolicyStatus.ACTIVE) {
                policy.setStatus(PolicyStatus.AWAITING_PAYMENT);
            } else {

                policy.setStatus(newStatus);
            }

            Policy updatedPolicy = policyRepository.save(policy);
            sendNotification(updatedPolicy);
            return updatedPolicy;
        });
    }

    private void sendNotification(Policy policy) {
        String userEmail = userClient.getEmailForUser(policy.getUsername());
        if (userEmail != null) {
            String subject = "Update on your InsureS3App Policy #" + policy.getPolicyNumber();
            String body;


            switch (policy.getStatus()) {
                case AWAITING_PAYMENT:
                    body = String.format(
                            "Dear %s,\n\nGreat news! Your policy application has been approved.\n\n" +
                                    "Policy Number: %s\n" +
                                    "Next Step: Please log in to your account and pay the premium of ₹%.2f to activate your coverage.\n\n" +
                                    "Thank you,\nThe InsureS3App Team",
                            policy.getUsername(), policy.getPolicyNumber(), policy.getPremium(), policy.getPolicyType()
                    );
                    break;
                case ACTIVE:
                    body = String.format(
                            "Dear %s,\n\n Your premium of ₹%.2f amount has been successfully received against this Policy. \n\n" +
                                    "Your policy is now active and you are covered.\n\n" +
                                    "Policy Number: %s\nStatus: ACTIVE\n\n" +
                                    "You can now file claims against this policy.\n\n" +
                                    "Thank you,\nThe InsureS3App Team",
                            policy.getUsername(), policy.getPolicyNumber() ,policy.getPolicyType(),policy.getPremium()
                    );
                    break;
                default: // For CANCELLED, etc.
                    body = String.format(
                            "Dear %s,\n\nThis is an update on your policy.\n\n" +
                                    "Policy Number: %s\nNew Status: %s\n\n" +
                                    "Please contact support for more information.\n\n" +
                                    "Thank you,\nThe InsureS3App Team",
                            policy.getUsername(), policy.getPolicyNumber(), policy.getStatus() ,policy.getPolicyType(),policy.getPremium()
                    );
                    break;
            }
            notificationClient.sendEmail(userEmail, subject, body);
        }
    }

    @Transactional
    public CancellationRequest requestCancellation(Long policyId, String username, String reason) {
        Policy policy = policyRepository.findByIdAndUsername(policyId, username)
                .orElseThrow(() -> new IllegalArgumentException("Policy not found or does not belong to user."));

        if (policy.getStatus() != PolicyStatus.ACTIVE) {
            throw new IllegalStateException("Only active policies can be requested for cancellation.");
        }

        // Update the policy status
        policy.setStatus(PolicyStatus.CANCELLATION_REQUESTED);
        policyRepository.save(policy);

        // Create and save the cancellation request record
        CancellationRequest request = new CancellationRequest();
        request.setPolicyId(policyId);
        request.setUsername(username);
        request.setReason(reason);
        return cancellationRequestRepository.save(request);
    }

    public List<CancellationRequest> getPendingCancellations() {
        return cancellationRequestRepository.findByStatus("PENDING");
    }

    @Transactional
    public Policy approveCancellation(Long requestId) {
        CancellationRequest request = cancellationRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Cancellation request not found."));

        // Update the request status
        request.setStatus("APPROVED");
        cancellationRequestRepository.save(request);

        // Update the policy status and send notification
        return updatePolicyStatus(request.getPolicyId(), PolicyStatus.CANCELLED)
                .orElseThrow(() -> new IllegalStateException("Policy could not be updated."));
    }

    @Transactional
    public void closePolicyByAdmin(Long policyId, String reason) {
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new IllegalArgumentException("Policy not found with id: " + policyId));

        if (policy.getStatus() != PolicyStatus.ACTIVE) {
            throw new IllegalStateException("Only active policies can be closed by an admin.");
        }

        policy.setStatus(PolicyStatus.CANCELLED);
        policyRepository.save(policy);

        sendPolicyCancellationNotificationByAdmin(policy, reason);
    }



    private void sendPolicyCancellationNotificationByAdmin(Policy policy, String reason) {
        String userEmail = userClient.getEmailForUser(policy.getUsername());
        if (userEmail != null) {
            String subject = "Important: Your InsureS3App Policy #" + policy.getPolicyNumber() + " Has Been Closed";
            String body = String.format(
                    "Dear %s,\n\nThis email is to inform you that your policy has been closed by an administrator.\n\n" +
                            "Policy Number: %s\n" +
                            "Policy Type: %s\n" +
                            "Status: CANCELLED\n\n" +
                            "Reason provided by administrator: %s\n\n" +
                            "If you have any questions, please contact our support team.\n\n" +
                            "Thank you,\nThe InsureS3App Team",
                    policy.getUsername(),
                    policy.getPolicyNumber(),
                    policy.getPolicyType(),
                    reason
            );
            notificationClient.sendEmail(userEmail, subject, body);
        }
    }

    @Transactional
    public void activatePolicy(Long policyId) {
        policyRepository.findById(policyId).ifPresent(policy -> {
            if (policy.getStatus() == PolicyStatus.AWAITING_PAYMENT) {
                policy.setStatus(PolicyStatus.ACTIVE);
                policy.setStartDate(LocalDate.now()); // Set the official start date
                policy.setEndDate(LocalDate.now().plusYears(1));
                Policy activatedPolicy = policyRepository.save(policy);
                sendNotification(activatedPolicy); // Send the "Your policy is now active" email
            }
        });
    }



    public Map<String, Object> getAdminDashboardStats() {
        List<Policy> allPolicies = policyRepository.findAll();

        Map<PolicyStatus, Long> countsByStatus = allPolicies.stream()
                .collect(Collectors.groupingBy(Policy::getStatus, Collectors.counting()));


        double totalAnnualRevenue = allPolicies.stream()
                .filter(p -> p.getStatus() == PolicyStatus.ACTIVE && p.getPremium() > 0)
                .mapToDouble(p -> p.getPremium() * 12)
                .sum();

        return Map.of(
                "countsByStatus", countsByStatus,
                "totalPolicies", (long) allPolicies.size(),
                "totalAnnualRevenue", totalAnnualRevenue
        );
    }

}