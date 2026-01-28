package com.stepin.jobopening.controller;

import com.stepin.jobopening.dto.JobOpeningCreateRequest;
import com.stepin.jobopening.dto.JobOpeningResponse;
import com.stepin.jobopening.dto.JobOpeningUpdateRequest;
import com.stepin.jobopening.service.JobOpeningService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class JobOpeningController {

    private final JobOpeningService jobOpeningService;

    public JobOpeningController(JobOpeningService jobOpeningService) {
        this.jobOpeningService = jobOpeningService;
    }

    /**
     * Lists all job openings for a specific campaign and company.
     *
     * @param campaignId the campaign UUID
     * @param companyId  the company UUID
     * @return list of JobOpeningResponse DTOs
     */
    @GetMapping("/campaigns/{campaignId}/companies/{companyId}/job-openings")
    public List<JobOpeningResponse> listJobOpenings(
            @PathVariable UUID campaignId,
            @PathVariable UUID companyId) {
        return jobOpeningService.listJobOpenings(campaignId, companyId);
    }

    /**
     * Lists all job openings for a specific company across all campaigns.
     *
     * @param companyId the company UUID
     * @return list of JobOpeningResponse DTOs
     */
    @GetMapping("/companies/{companyId}/job-openings")
    public List<JobOpeningResponse> listJobOpeningsByCompany(@PathVariable UUID companyId) {
        return jobOpeningService.listJobOpeningsByCompany(companyId);
    }

    /**
     * Lists all job openings for a specific campaign across all companies.
     *
     * @param campaignId the campaign UUID
     * @return list of JobOpeningResponse DTOs
     */
    @GetMapping("/campaigns/{campaignId}/job-openings")
    public List<JobOpeningResponse> listJobOpeningsByCampaign(@PathVariable UUID campaignId) {
        return jobOpeningService.listJobOpeningsByCampaign(campaignId);
    }

    /**
     * Creates a new job opening.
     *
     * @param campaignId the campaign UUID
     * @param companyId  the company UUID
     * @param request    the job opening creation request
     * @return the created JobOpeningResponse DTO
     */
    @PostMapping("/campaigns/{campaignId}/companies/{companyId}/job-openings")
    @ResponseStatus(HttpStatus.CREATED)
    public JobOpeningResponse createJobOpening(
            @PathVariable UUID campaignId,
            @PathVariable UUID companyId,
            @Valid @RequestBody JobOpeningCreateRequest request) {
        return jobOpeningService.createJobOpening(campaignId, companyId, request);
    }

    /**
     * Updates an existing job opening.
     *
     * @param campaignId the campaign UUID
     * @param companyId  the company UUID
     * @param jobId      the job opening UUID
     * @param request    the job opening update request
     * @return the updated JobOpeningResponse DTO
     */
    @PutMapping("/campaigns/{campaignId}/companies/{companyId}/job-openings/{jobId}")
    public JobOpeningResponse updateJobOpening(
            @PathVariable UUID campaignId,
            @PathVariable UUID companyId,
            @PathVariable UUID jobId,
            @Valid @RequestBody JobOpeningUpdateRequest request) {
        return jobOpeningService.updateJobOpening(campaignId, companyId, jobId, request);
    }

    /**
     * Deletes a job opening.
     *
     * @param campaignId the campaign UUID
     * @param companyId  the company UUID
     * @param jobId      the job opening UUID
     */
    @DeleteMapping("/campaigns/{campaignId}/companies/{companyId}/job-openings/{jobId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteJobOpening(
            @PathVariable UUID campaignId,
            @PathVariable UUID companyId,
            @PathVariable UUID jobId) {
        jobOpeningService.deleteJobOpening(campaignId, companyId, jobId);
    }

    /**
     * Gets a specific job opening by ID.
     *
     * @param jobId the job opening UUID
     * @return the JobOpeningResponse DTO
     */
    @GetMapping("/job-openings/{jobId}")
    public JobOpeningResponse getJobOpening(@PathVariable UUID jobId) {
        return jobOpeningService.getJobOpening(jobId);
    }

    /**
     * Updates a job opening by ID.
     *
     * @param jobId   the job opening UUID
     * @param request the job opening update request
     * @return the updated JobOpeningResponse DTO
     */
    @PutMapping("/job-openings/{jobId}")
    public JobOpeningResponse updateJobOpeningById(
            @PathVariable UUID jobId,
            @Valid @RequestBody JobOpeningUpdateRequest request) {
        return jobOpeningService.updateJobOpeningById(jobId, request);
    }

    /**
     * Deletes a job opening by ID.
     *
     * @param jobId the job opening UUID
     */
    @DeleteMapping("/job-openings/{jobId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteJobOpeningById(@PathVariable UUID jobId) {
        jobOpeningService.deleteJobOpeningById(jobId);
    }
}
