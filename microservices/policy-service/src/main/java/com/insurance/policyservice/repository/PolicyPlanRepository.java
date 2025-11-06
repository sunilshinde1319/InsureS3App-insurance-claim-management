package com.insurance.policyservice.repository;

import com.insurance.policyservice.entity.PolicyPlan;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyPlanRepository extends JpaRepository<PolicyPlan, Long> {


    List<PolicyPlan> findByPolicyType(String policyType);
    @Transactional
    void deleteByPolicyType(String policyType);}