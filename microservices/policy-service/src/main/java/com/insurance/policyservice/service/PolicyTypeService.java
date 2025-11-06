package com.insurance.policyservice.service;

import com.insurance.policyservice.client.NotificationClient;
import com.insurance.policyservice.client.UserClient;
import com.insurance.policyservice.dto.PolicyTypeDto;
import com.insurance.policyservice.entity.Policy;
import com.insurance.policyservice.entity.PolicyStatus;
import com.insurance.policyservice.entity.PolicyType;
import com.insurance.policyservice.repository.PolicyPlanRepository;
import com.insurance.policyservice.repository.PolicyRepository;
import com.insurance.policyservice.repository.PolicyTypeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PolicyTypeService {

    @Autowired private PolicyTypeRepository policyTypeRepository;
    @Autowired private PolicyRepository policyRepository;
    @Autowired private PolicyPlanRepository policyPlanRepository;
    @Autowired private UserClient userClient;
    @Autowired private NotificationClient notificationClient;


    public List<PolicyType> getEnabledPolicyTypes() {
        return policyTypeRepository.findByIsEnabled(true);
    }

    // For Admins
    public List<PolicyType> getAllPolicyTypes() {
        return policyTypeRepository.findAll();
    }

    public PolicyType createPolicyType(PolicyTypeDto dto) {
        policyTypeRepository.findByNameIgnoreCase(dto.name())
                .ifPresent(pt -> { throw new IllegalStateException("Policy type with this name already exists."); });

        PolicyType newType = new PolicyType();
        newType.setName(dto.name());
        newType.setIconName(dto.iconName());
        newType.setDescription(dto.description());
        newType.setIsEnabled(dto.isEnabled());
        return policyTypeRepository.save(newType);
    }

    public PolicyType updatePolicyType(Long id, PolicyTypeDto dto) {
        PolicyType existingType = policyTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Policy type not found."));

        existingType.setName(dto.name());
        existingType.setIconName(dto.iconName());
        existingType.setDescription(dto.description());
        existingType.setIsEnabled(dto.isEnabled());
        return policyTypeRepository.save(existingType);
    }

    @Transactional
    public void deletePolicyType(Long id, String reason) {
        PolicyType typeToDelete = policyTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Policy type not found."));

        List<Policy> activePolicies = policyRepository.findByPolicyTypeAndStatus(typeToDelete.getName(), PolicyStatus.ACTIVE);


        if (!activePolicies.isEmpty()) {

            List<String> usernames = activePolicies.stream().map(Policy::getUsername).distinct().collect(Collectors.toList());
            for (String username : usernames) {
                sendPolicyDeletionNotification(username, typeToDelete.getName(), reason);
            }
        }

        policyPlanRepository.deleteByPolicyType(typeToDelete.getName()); // We will add this repository method
        policyTypeRepository.deleteById(id);
    }

    private void sendPolicyDeletionNotification(String username, String policyTypeName, String reason) {
        String userEmail = userClient.getEmailForUser(username);
        if (userEmail != null) {
            String subject = "Important Update: Your " + policyTypeName + " Policy Type";
            String body = String.format(
                    "Dear %s,\n\nThis is to inform you that the insurance product type '%s' is being discontinued by InsureS3App.\n\n" +
                            "Reason provided by administrator: %s\n\n" +
                            "This action affects your active policy/policies of this type. Please log in to your account to view your policy status or contact support for more information on how this affects your coverage.\n\n" +
                            "The InsureS3App Team",
                    username, policyTypeName, reason
            );
            notificationClient.sendEmail(userEmail, subject, body);
        }
    }
}