package com.influcollab.dto;

public class LoginResponse {

    private String token;
    private UserSummaryDTO user;

    public LoginResponse() {
    }

    public LoginResponse(String token, UserSummaryDTO user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserSummaryDTO getUser() {
        return user;
    }

    public void setUser(UserSummaryDTO user) {
        this.user = user;
    }
}
