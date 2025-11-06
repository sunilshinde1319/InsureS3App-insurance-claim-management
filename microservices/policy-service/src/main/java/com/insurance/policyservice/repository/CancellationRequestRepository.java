package com.insurance.policyservice.repository;

import com.insurance.policyservice.entity.CancellationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CancellationRequestRepository extends JpaRepository<CancellationRequest, Long> {
    List<CancellationRequest> findByStatus(String status);
}