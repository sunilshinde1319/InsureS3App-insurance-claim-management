package com.insurance.documentservice.repository;

import com.insurance.documentservice.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


import com.insurance.documentservice.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByClaimId(Long claimId);


    List<Document> findByPolicyId(Long policyId);


    List<Document> findByPendingReferenceId(String pendingReferenceId);
    Optional<Document> findByStoredFilename(String storedFilename);
}