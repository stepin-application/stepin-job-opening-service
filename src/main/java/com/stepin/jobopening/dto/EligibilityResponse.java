package com.stepin.jobopening.dto;

public class EligibilityResponse {

    private boolean canMutateJobs;
    private String reason;

    public EligibilityResponse() {
    }

    public EligibilityResponse(boolean canMutateJobs, String reason) {
        this.canMutateJobs = canMutateJobs;
        this.reason = reason;
    }

    public boolean isCanMutateJobs() {
        return canMutateJobs;
    }

    public void setCanMutateJobs(boolean canMutateJobs) {
        this.canMutateJobs = canMutateJobs;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
