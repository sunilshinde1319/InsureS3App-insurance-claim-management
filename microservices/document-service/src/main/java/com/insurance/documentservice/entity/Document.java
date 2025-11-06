package com.insurance.documentservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String originalFilename;
    @Column(nullable = false, unique = true)
    private String storedFilename;
    @Column(nullable = false)
    private String contentType;
    @Column(nullable = false)
    private String filePath;

    @Column(nullable = true)
    private Long claimId;
    @Column(nullable = true)
    private Long policyId;
    @Column(nullable = true)
    private String pendingReferenceId;
    // Constructors
    public Document() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
    public String getStoredFilename() { return storedFilename; }
    public void setStoredFilename(String storedFilename) { this.storedFilename = storedFilename; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public Long getClaimId() { return claimId; }
    public void setClaimId(Long claimId) { this.claimId = claimId; }
    public Long getPolicyId() { return policyId; }
    public void setPolicyId(Long policyId) { this.policyId = policyId; }
    public String getPendingReferenceId() { return pendingReferenceId; }
    public void setPendingReferenceId(String pendingReferenceId) { this.pendingReferenceId = pendingReferenceId; }
}