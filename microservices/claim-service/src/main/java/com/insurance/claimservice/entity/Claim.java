package com.insurance.claimservice.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "claims")
@Data
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String claimNumber;

    @Column(nullable = false)
    private String description;

    private LocalDate claimDate;


    @Column(nullable = false)
    private Long policyId;

    private double claimAmount;


    @Enumerated(EnumType.STRING)
    private ClaimStatus status;


    @Column(nullable = false)
    private String username;

    @OneToMany(mappedBy = "claim", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    @OrderBy("timestamp ASC")
    private List<ClaimHistory> history = new ArrayList<>();

    public Claim() {
    }

    public Claim(Long id, String claimNumber, String description, LocalDate claimDate, Long policyId, double claimAmount, ClaimStatus status, String username) {
        this.id = id;
        this.claimNumber = claimNumber;
        this.description = description;
        this.claimDate = claimDate;
        this.policyId = policyId;
        this.claimAmount = claimAmount;
        this.status = status;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClaimNumber() {
        return claimNumber;
    }

    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(LocalDate claimDate) {
        this.claimDate = claimDate;
    }

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public double getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(double claimAmount) {
        this.claimAmount = claimAmount;
    }

    public ClaimStatus getStatus() {
        return status;
    }

    public void setStatus(ClaimStatus status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<ClaimHistory> getHistory() { return history; }
    public void setHistory(List<ClaimHistory> history) { this.history = history; }


    public void addHistory(ClaimStatus status, String notes) {
        ClaimHistory historyEntry = new ClaimHistory(this, status, notes);
        this.history.add(historyEntry);
    }
}