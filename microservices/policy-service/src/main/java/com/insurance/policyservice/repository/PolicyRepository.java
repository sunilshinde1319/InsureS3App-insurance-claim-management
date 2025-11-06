package com.insurance.policyservice.repository;

import com.insurance.policyservice.entity.Policy;
import com.insurance.policyservice.entity.PolicyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {
    List<Policy> findByUsername(String username);

    Optional<Policy> findByIdAndUsername(Long id, String username);


    @Query("SELECT p FROM Policy p WHERE p.username = :username AND " +
            "(LOWER(p.policyNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.policyType) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Policy> searchByUsernameAndQuery(@Param("username") String username, @Param("query") String query);

    List<Policy> findByPolicyTypeAndStatus(String policyType, PolicyStatus status);
}