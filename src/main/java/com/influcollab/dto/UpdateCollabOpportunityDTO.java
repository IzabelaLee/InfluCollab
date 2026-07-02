package com.influcollab.dto;

import java.time.LocalDate;

public class UpdateCollabOpportunityDTO {
    private String title;
    private String city;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;

    public UpdateCollabOpportunityDTO() {
    }

    public UpdateCollabOpportunityDTO(String title, String city, LocalDate startDate, 
                                       LocalDate endDate, String description) {
        this.title = title;
        this.city = city;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
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
}
