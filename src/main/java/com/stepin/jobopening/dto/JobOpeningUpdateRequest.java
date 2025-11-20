package com.stepin.jobopening.dto;

import jakarta.validation.constraints.NotBlank;

public class JobOpeningUpdateRequest {

    @NotBlank
    private String title;
    private String description;
    private String requirements;

    public JobOpeningUpdateRequest() {
    }

    public JobOpeningUpdateRequest(String title, String description, String requirements) {
        this.title = title;
        this.description = description;
        this.requirements = requirements;
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
}
