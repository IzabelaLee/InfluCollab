package com.influcollab.dto;

public class UserSummaryDTO {
    private Long id;
    private String name;
    private String channelName;

    public UserSummaryDTO() {
    }

    public UserSummaryDTO(Long id, String name, String channelName) {
        this.id = id;
        this.name = name;
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

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
