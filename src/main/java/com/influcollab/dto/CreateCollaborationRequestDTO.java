package com.influcollab.dto;

import jakarta.validation.constraints.NotNull;

public class CreateCollaborationRequestDTO {

    @NotNull
    private Long senderId;

    @NotNull
    private String message;

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
