package com.influcollab.dto;

import java.time.LocalDate;

public class CollabOpportunityDTO {
    private Long id;
    private String title;
    private String city;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private LocalDate createdAt;
    private UserSummaryDTO creator;

    public CollabOpportunityDTO() {
    }

    public CollabOpportunityDTO(Long id, String title, String city, LocalDate startDate, 
                                 LocalDate endDate, String description, LocalDate createdAt, 
                                 UserSummaryDTO creator) {
        this.id = id;
        this.title = title;
        this.city = city;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.createdAt = createdAt;
        this.creator = creator;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public UserSummaryDTO getCreator() {
        return creator;
    }

    public void setCreator(UserSummaryDTO creator) {
        this.creator = creator;
    }
}
