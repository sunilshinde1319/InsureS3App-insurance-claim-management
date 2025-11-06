package com.insurance.policyservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "policy_plans")
public class PolicyPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String policyType;

    @Column(nullable = false)
    private String planName;

    @Column(columnDefinition = "TEXT")
    private String description;


    private double baseCoverage;

    private double basePremium;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPolicyType() { return policyType; }
    public void setPolicyType(String policyType) { this.policyType = policyType; }
    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getBaseCoverage() { return baseCoverage; }
    public void setBaseCoverage(double baseCoverage) { this.baseCoverage = baseCoverage; }
    public double getBasePremium() { return basePremium; }
    public void setBasePremium(double basePremium) { this.basePremium = basePremium; }
}