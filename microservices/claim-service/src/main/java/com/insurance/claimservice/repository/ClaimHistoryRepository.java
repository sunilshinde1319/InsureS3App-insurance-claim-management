package com.insurance.claimservice.repository;

import com.insurance.claimservice.entity.ClaimHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimHistoryRepository extends JpaRepository<ClaimHistory, Long> {
}