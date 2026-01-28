package com.stepin.jobopening.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class JobOpeningResponse {

    private UUID id;
    private UUID campaignId;
    private UUID companyId;
    private String title;
    private String description;
    private String contractType;
    private String duration;
    private String location;
    private Integer maxParticipants;
    private String requirements;
    private String benefits;
    private String tags;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public JobOpeningResponse() {
    }

    public JobOpeningResponse(UUID id, UUID campaignId, UUID companyId, String title, String description,
            String contractType, String duration, String location, Integer maxParticipants,
            String requirements, String benefits, String tags, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.campaignId = campaignId;
        this.companyId = companyId;
        this.title = title;
        this.description = description;
        this.contractType = contractType;
        this.duration = duration;
        this.location = location;
        this.maxParticipants = maxParticipants;
        this.requirements = requirements;
        this.benefits = benefits;
        this.tags = tags;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(UUID campaignId) {
        this.campaignId = campaignId;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
    }

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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
