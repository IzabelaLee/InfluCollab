package com.influcollab.dto;

public class UserSummaryDTO {
    private Long id;
    private String name;
    private String email;
    private String channelName;

    public UserSummaryDTO() {
    }

    public UserSummaryDTO(Long id, String name, String email, String channelName) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.channelName = channelName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
