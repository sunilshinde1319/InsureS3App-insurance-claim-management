package com.insurance.policyservice.repository;

import com.insurance.policyservice.entity.PolicyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyTypeRepository extends JpaRepository<PolicyType, Long> {
    List<PolicyType> findByIsEnabled(boolean isEnabled);

    Optional<PolicyType> findByNameIgnoreCase(String name);
}