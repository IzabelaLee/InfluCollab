package com.influcollab.dto;

import com.influcollab.enums.CollaborationRequestStatus;

import java.time.LocalDateTime;

public class CollaborationRequestDTO {

    private Long id;

    private Long senderId;

    private Long opportunityId;

    private String message;

    private CollaborationRequestStatus status;

    private LocalDateTime createdAt;

    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return id;
    }
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }
    public Long getSenderId() {
        return senderId;
    }
    public void setOpportunityId(Long opportunityId) {
        this.opportunityId = opportunityId;
    }
    public Long getOpportunityId() {
        return opportunityId;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
    public void setStatus(CollaborationRequestStatus status) {
        this.status = status;
    }
    public CollaborationRequestStatus getStatus() {
        return status;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

}
