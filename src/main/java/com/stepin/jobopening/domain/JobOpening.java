package com.stepin.jobopening.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

public class JobOpening {
    private UUID id;
    private UUID campaignId;
    private UUID companyId;
    private String title;
    private String description;
    private String requirements;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public JobOpening() {
    }

    public JobOpening(UUID id, UUID campaignId, UUID companyId, String title, String description,
            String requirements, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.campaignId = campaignId;
        this.companyId = companyId;
        this.title = title;
        this.description = description;
        this.requirements = requirements;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

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

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
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
