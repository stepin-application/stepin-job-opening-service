package com.stepin.jobopening.dto;

import jakarta.validation.constraints.NotBlank;

public class JobOpeningCreateRequest {

    @NotBlank
    private String title;
    @NotBlank
    private String description;
    private String contractType;
    private String duration;
    private String location;
    private Integer maxParticipants;
    private String requirements;
    private String benefits;
    private String tags;

    public JobOpeningCreateRequest() {
    }

    public JobOpeningCreateRequest(String title, String description, String contractType, 
            String duration, String location, Integer maxParticipants, String requirements, 
            String benefits, String tags) {
        this.title = title;
        this.description = description;
        this.contractType = contractType;
        this.duration = duration;
        this.location = location;
        this.maxParticipants = maxParticipants;
        this.requirements = requirements;
        this.benefits = benefits;
        this.tags = tags;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
