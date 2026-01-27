package com.stepin.jobopening.service;

import com.stepin.jobopening.client.CampaignServiceClient;
import com.stepin.jobopening.domain.JobOpening;
import com.stepin.jobopening.dto.EligibilityResponse;
import com.stepin.jobopening.dto.JobOpeningCreateRequest;
import com.stepin.jobopening.dto.JobOpeningResponse;
import com.stepin.jobopening.dto.JobOpeningUpdateRequest;
import com.stepin.jobopening.exception.BusinessException;
import com.stepin.jobopening.exception.ResourceNotFoundException;
import com.stepin.jobopening.mapper.JobOpeningMapper;
import com.stepin.jobopening.repository.JobOpeningRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JobOpeningService {

    private static final Logger logger = LoggerFactory.getLogger(JobOpeningService.class);

    private final JobOpeningRepository jobOpeningRepository;
    private final CampaignServiceClient campaignServiceClient;
    private final JobOpeningMapper mapper = JobOpeningMapper.INSTANCE;

    public JobOpeningService(JobOpeningRepository jobOpeningRepository,
            CampaignServiceClient campaignServiceClient) {
        this.jobOpeningRepository = jobOpeningRepository;
        this.campaignServiceClient = campaignServiceClient;
    }

    /**
     * Lists all job openings for a specific campaign and company.
     *
     * @param campaignId the campaign UUID
     * @param companyId  the company UUID
     * @return list of JobOpeningResponse DTOs
     */
    public List<JobOpeningResponse> listJobOpenings(UUID campaignId, UUID companyId) {
        logger.debug("Listing job openings for campaignId={}, companyId={}", campaignId, companyId);

        List<JobOpening> jobOpenings = jobOpeningRepository.findByCampaignIdAndCompanyId(campaignId, companyId);

        logger.debug("Found {} job openings", jobOpenings.size());

        return jobOpenings.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lists all job openings for a specific company.
     *
     * @param companyId the company UUID
     * @return list of JobOpeningResponse DTOs
     */
    public List<JobOpeningResponse> listJobOpeningsByCompany(UUID companyId) {
        logger.debug("Listing job openings for companyId={}", companyId);

        List<JobOpening> jobOpenings = jobOpeningRepository.findByCompanyId(companyId);

        logger.debug("Found {} job openings", jobOpenings.size());

        return jobOpenings.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Gets a job opening by id.
     *
     * @param jobId the job opening UUID
     * @return JobOpeningResponse DTO
     */
    public JobOpeningResponse getJobOpening(UUID jobId) {
        JobOpening jobOpening = jobOpeningRepository.findById(jobId)
                .orElseThrow(() -> {
                    logger.warn("Job opening not found: id={}", jobId);
                    return new ResourceNotFoundException("Job opening not found");
                });

        return mapper.toResponse(jobOpening);
    }

    /**
     * Updates an existing job opening by id.
     *
     * @param jobId   the job opening UUID
     * @param request the job opening update request
     * @return the updated JobOpeningResponse DTO
     */
    public JobOpeningResponse updateJobOpening(UUID jobId, JobOpeningUpdateRequest request) {
        JobOpening jobOpening = jobOpeningRepository.findById(jobId)
                .orElseThrow(() -> {
                    logger.warn("Job opening not found: id={}", jobId);
                    return new ResourceNotFoundException("Job opening not found");
                });

        EligibilityResponse eligibility = campaignServiceClient.checkEligibility(
                jobOpening.getCampaignId(), jobOpening.getCompanyId());
        if (!eligibility.isCanMutateJobs()) {
            logger.warn("Company not eligible to update job opening: {}", eligibility.getReason());
            throw new BusinessException(eligibility.getReason());
        }

        mapper.updateEntity(request, jobOpening);
        jobOpening.setUpdatedAt(OffsetDateTime.now());

        JobOpening updatedJobOpening = jobOpeningRepository.save(jobOpening);

        logger.info("Updated job opening with id={}", updatedJobOpening.getId());

        return mapper.toResponse(updatedJobOpening);
    }

    /**
     * Creates a new job opening after checking eligibility.
     *
     * @param campaignId the campaign UUID
     * @param companyId  the company UUID
     * @param request    the job opening creation request
     * @return the created JobOpeningResponse DTO
     * @throws BusinessException if the company is not eligible to create job
     *                           openings
     */
    public JobOpeningResponse createJobOpening(UUID campaignId, UUID companyId,
            JobOpeningCreateRequest request) {
        logger.debug("Creating job opening for campaignId={}, companyId={}, title={}",
                campaignId, companyId, request.getTitle());

        // Check eligibility
        EligibilityResponse eligibility = campaignServiceClient.checkEligibility(campaignId, companyId);
        if (!eligibility.isCanMutateJobs()) {
            logger.warn("Company not eligible to create job opening: {}", eligibility.getReason());
            throw new BusinessException(eligibility.getReason());
        }

        // Create job opening
        JobOpening jobOpening = mapper.toEntity(request);
        jobOpening.setId(UUID.randomUUID());
        jobOpening.setCampaignId(campaignId);
        jobOpening.setCompanyId(companyId);

        OffsetDateTime now = OffsetDateTime.now();
        jobOpening.setCreatedAt(now);
        jobOpening.setUpdatedAt(now);

        JobOpening savedJobOpening = jobOpeningRepository.save(jobOpening);

        logger.info("Created job opening with id={}", savedJobOpening.getId());

        return mapper.toResponse(savedJobOpening);
    }

    /**
     * Updates an existing job opening after checking eligibility.
     *
     * @param campaignId the campaign UUID
     * @param companyId  the company UUID
     * @param jobId      the job opening UUID
     * @param request    the job opening update request
     * @return the updated JobOpeningResponse DTO
     * @throws BusinessException         if the company is not eligible to update
     *                                   job openings
     * @throws ResourceNotFoundException if the job opening is not found
     */
    public JobOpeningResponse updateJobOpening(UUID campaignId, UUID companyId, UUID jobId,
            JobOpeningUpdateRequest request) {
        logger.debug("Updating job opening id={} for campaignId={}, companyId={}",
                jobId, campaignId, companyId);

        // Check eligibility
        EligibilityResponse eligibility = campaignServiceClient.checkEligibility(campaignId, companyId);
        if (!eligibility.isCanMutateJobs()) {
            logger.warn("Company not eligible to update job opening: {}", eligibility.getReason());
            throw new BusinessException(eligibility.getReason());
        }

        // Find job opening
        JobOpening jobOpening = jobOpeningRepository
                .findByIdAndCampaignIdAndCompanyId(jobId, campaignId, companyId)
                .orElseThrow(() -> {
                    logger.warn("Job opening not found: id={}, campaignId={}, companyId={}",
                            jobId, campaignId, companyId);
                    return new ResourceNotFoundException("Job opening not found");
                });

        // Update fields
        mapper.updateEntity(request, jobOpening);
        jobOpening.setUpdatedAt(OffsetDateTime.now());

        JobOpening updatedJobOpening = jobOpeningRepository.save(jobOpening);

        logger.info("Updated job opening with id={}", updatedJobOpening.getId());

        return mapper.toResponse(updatedJobOpening);
    }

    /**
     * Deletes a job opening after checking eligibility.
     *
     * @param campaignId the campaign UUID
     * @param companyId  the company UUID
     * @param jobId      the job opening UUID
     * @throws BusinessException         if the company is not eligible to delete
     *                                   job openings
     * @throws ResourceNotFoundException if the job opening is not found
     */
    public void deleteJobOpening(UUID campaignId, UUID companyId, UUID jobId) {
        logger.debug("Deleting job opening id={} for campaignId={}, companyId={}",
                jobId, campaignId, companyId);

        // Check eligibility
        EligibilityResponse eligibility = campaignServiceClient.checkEligibility(campaignId, companyId);
        if (!eligibility.isCanMutateJobs()) {
            logger.warn("Company not eligible to delete job opening: {}", eligibility.getReason());
            throw new BusinessException(eligibility.getReason());
        }

        // Find job opening
        JobOpening jobOpening = jobOpeningRepository
                .findByIdAndCampaignIdAndCompanyId(jobId, campaignId, companyId)
                .orElseThrow(() -> {
                    logger.warn("Job opening not found: id={}, campaignId={}, companyId={}",
                            jobId, campaignId, companyId);
                    return new ResourceNotFoundException("Job opening not found");
                });

        // Delete job opening
        jobOpeningRepository.delete(jobOpening);

        logger.info("Deleted job opening with id={}", jobId);
    }

    /**
     * Deletes a job opening by id.
     *
     * @param jobId the job opening UUID
     */
    public void deleteJobOpening(UUID jobId) {
        JobOpening jobOpening = jobOpeningRepository.findById(jobId)
                .orElseThrow(() -> {
                    logger.warn("Job opening not found: id={}", jobId);
                    return new ResourceNotFoundException("Job opening not found");
                });

        EligibilityResponse eligibility = campaignServiceClient.checkEligibility(
                jobOpening.getCampaignId(), jobOpening.getCompanyId());
        if (!eligibility.isCanMutateJobs()) {
            logger.warn("Company not eligible to delete job opening: {}", eligibility.getReason());
            throw new BusinessException(eligibility.getReason());
        }

        jobOpeningRepository.delete(jobOpening);

        logger.info("Deleted job opening with id={}", jobId);
    }
}
