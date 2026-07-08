package com.influcollab.entity;

import com.influcollab.enums.CollaborationRequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "collaboration_requests")
public class CollaborationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "opportunity_id", nullable = false)
    private CollabOpportunity opportunity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CollaborationRequestStatus status = CollaborationRequestStatus.PENDING;

    @NotBlank(message = "Message is required")
    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public CollaborationRequest() {
    }

    public CollaborationRequest(User sender, CollabOpportunity opportunity, String message) {
        this.sender = sender;
        this.opportunity = opportunity;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public CollabOpportunity getOpportunity() {
        return opportunity;
    }

    public void setOpportunity(CollabOpportunity opportunity) {
        this.opportunity = opportunity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CollaborationRequestStatus getStatus() {
        return status;
    }

    public void setStatus(CollaborationRequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
