package com.insurance.userservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
public class Feedback {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private int rating;
    @Column(nullable = false)
    private String tag;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;
    private LocalDateTime receivedAt = LocalDateTime.now();

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public LocalDateTime getReceivedAt() { return receivedAt; }
    public void setReceivedAt(LocalDateTime receivedAt) { this.receivedAt = receivedAt; }
}