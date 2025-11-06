package com.insurance.claimservice.repository;

import com.insurance.claimservice.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {

    List<Claim> findByUsername(String username);

    Optional<Claim> findByIdAndUsername(Long id, String username);
    List<Claim> findByUsernameContainingIgnoreCase(String username);
}