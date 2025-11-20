package com.stepin.jobopening.service;

import com.stepin.jobopening.client.CampaignServiceClient;
import com.stepin.jobopening.domain.JobOpening;
import com.stepin.jobopening.dto.EligibilityResponse;
import com.stepin.jobopening.dto.JobOpeningCreateRequest;
import com.stepin.jobopening.dto.JobOpeningResponse;
import com.stepin.jobopening.dto.JobOpeningUpdateRequest;
import com.stepin.jobopening.exception.BusinessException;
import com.stepin.jobopening.exception.ResourceNotFoundException;
import com.stepin.jobopening.repository.JobOpeningRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobOpeningServiceTest {

    @Mock
    private JobOpeningRepository jobOpeningRepository;

    @Mock
    private CampaignServiceClient campaignServiceClient;

    @InjectMocks
    private JobOpeningService jobOpeningService;

    private UUID campaignId;
    private UUID companyId;
    private UUID jobId;
    private JobOpening jobOpening;

    @BeforeEach
    void setUp() {
        campaignId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        companyId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        jobId = UUID.fromString("33333333-3333-3333-3333-333333333333");

        jobOpening = new JobOpening();
        jobOpening.setId(jobId);
        jobOpening.setCampaignId(campaignId);
        jobOpening.setCompanyId(companyId);
        jobOpening.setTitle("Software Engineer");
        jobOpening.setDescription("Develop software");
        jobOpening.setRequirements("Java, Spring Boot");
        jobOpening.setCreatedAt(OffsetDateTime.now());
        jobOpening.setUpdatedAt(OffsetDateTime.now());
    }

    @Test
    void listJobOpenings_shouldReturnListOfJobOpenings() {
        // Arrange
        JobOpening jobOpening2 = new JobOpening();
        jobOpening2.setId(UUID.randomUUID());
        jobOpening2.setCampaignId(campaignId);
        jobOpening2.setCompanyId(companyId);
        jobOpening2.setTitle("DevOps Engineer");
        jobOpening2.setDescription("Manage infrastructure");
        jobOpening2.setRequirements("Docker, Kubernetes");
        jobOpening2.setCreatedAt(OffsetDateTime.now());
        jobOpening2.setUpdatedAt(OffsetDateTime.now());

        List<JobOpening> jobOpenings = Arrays.asList(jobOpening, jobOpening2);
        when(jobOpeningRepository.findByCampaignIdAndCompanyId(campaignId, companyId))
                .thenReturn(jobOpenings);

        // Act
        List<JobOpeningResponse> result = jobOpeningService.listJobOpenings(campaignId, companyId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Software Engineer", result.get(0).getTitle());
        assertEquals("DevOps Engineer", result.get(1).getTitle());
        verify(jobOpeningRepository).findByCampaignIdAndCompanyId(campaignId, companyId);
    }

    @Test
    void listJobOpenings_shouldReturnEmptyListWhenNoJobOpenings() {
        // Arrange
        when(jobOpeningRepository.findByCampaignIdAndCompanyId(campaignId, companyId))
                .thenReturn(Arrays.asList());

        // Act
        List<JobOpeningResponse> result = jobOpeningService.listJobOpenings(campaignId, companyId);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(jobOpeningRepository).findByCampaignIdAndCompanyId(campaignId, companyId);
    }

    @Test
    void createJobOpening_shouldCreateJobOpeningWhenEligible() {
        // Arrange
        JobOpeningCreateRequest request = new JobOpeningCreateRequest(
                "Software Engineer",
                "Develop software",
                "Java, Spring Boot");

        EligibilityResponse eligibility = new EligibilityResponse(true, "");
        when(campaignServiceClient.checkEligibility(campaignId, companyId)).thenReturn(eligibility);
        when(jobOpeningRepository.save(any(JobOpening.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        JobOpeningResponse result = jobOpeningService.createJobOpening(campaignId, companyId, request);

        // Assert
        assertNotNull(result);
        assertEquals("Software Engineer", result.getTitle());
        assertEquals("Develop software", result.getDescription());
        assertEquals("Java, Spring Boot", result.getRequirements());
        assertEquals(campaignId, result.getCampaignId());
        assertEquals(companyId, result.getCompanyId());
        assertNotNull(result.getId());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        verify(campaignServiceClient).checkEligibility(campaignId, companyId);
        verify(jobOpeningRepository).save(any(JobOpening.class));
    }

    @Test
    void createJobOpening_shouldThrowBusinessExceptionWhenNotEligible() {
        // Arrange
        JobOpeningCreateRequest request = new JobOpeningCreateRequest(
                "Software Engineer",
                "Develop software",
                "Java, Spring Boot");

        EligibilityResponse eligibility = new EligibilityResponse(false, "Campaign is locked");
        when(campaignServiceClient.checkEligibility(campaignId, companyId)).thenReturn(eligibility);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            jobOpeningService.createJobOpening(campaignId, companyId, request);
        });

        assertEquals("Campaign is locked", exception.getMessage());
        verify(campaignServiceClient).checkEligibility(campaignId, companyId);
        verify(jobOpeningRepository, never()).save(any(JobOpening.class));
    }

    @Test
    void createJobOpening_shouldThrowBusinessExceptionWhenDeadlinePassed() {
        // Arrange
        JobOpeningCreateRequest request = new JobOpeningCreateRequest(
                "Software Engineer",
                "Develop software",
                "Java, Spring Boot");

        EligibilityResponse eligibility = new EligibilityResponse(false, "Campaign deadline has passed");
        when(campaignServiceClient.checkEligibility(campaignId, companyId)).thenReturn(eligibility);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            jobOpeningService.createJobOpening(campaignId, companyId, request);
        });

        assertEquals("Campaign deadline has passed", exception.getMessage());
        verify(campaignServiceClient).checkEligibility(campaignId, companyId);
        verify(jobOpeningRepository, never()).save(any(JobOpening.class));
    }

    @Test
    void createJobOpening_shouldThrowBusinessExceptionWhenInvitationNotAccepted() {
        // Arrange
        JobOpeningCreateRequest request = new JobOpeningCreateRequest(
                "Software Engineer",
                "Develop software",
                "Java, Spring Boot");

        EligibilityResponse eligibility = new EligibilityResponse(false, "Company has not accepted invitation");
        when(campaignServiceClient.checkEligibility(campaignId, companyId)).thenReturn(eligibility);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            jobOpeningService.createJobOpening(campaignId, companyId, request);
        });

        assertEquals("Company has not accepted invitation", exception.getMessage());
        verify(campaignServiceClient).checkEligibility(campaignId, companyId);
        verify(jobOpeningRepository, never()).save(any(JobOpening.class));
    }

    @Test
    void updateJobOpening_shouldUpdateJobOpeningWhenEligible() {
        // Arrange
        JobOpeningUpdateRequest request = new JobOpeningUpdateRequest(
                "Senior Software Engineer",
                "Lead software development",
                "Java, Spring Boot, Microservices");

        EligibilityResponse eligibility = new EligibilityResponse(true, "");
        when(campaignServiceClient.checkEligibility(campaignId, companyId)).thenReturn(eligibility);
        when(jobOpeningRepository.findByIdAndCampaignIdAndCompanyId(jobId, campaignId, companyId))
                .thenReturn(Optional.of(jobOpening));
        when(jobOpeningRepository.save(any(JobOpening.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        JobOpeningResponse result = jobOpeningService.updateJobOpening(campaignId, companyId, jobId, request);

        // Assert
        assertNotNull(result);
        assertEquals("Senior Software Engineer", result.getTitle());
        assertEquals("Lead software development", result.getDescription());
        assertEquals("Java, Spring Boot, Microservices", result.getRequirements());

        verify(campaignServiceClient).checkEligibility(campaignId, companyId);
        verify(jobOpeningRepository).findByIdAndCampaignIdAndCompanyId(jobId, campaignId, companyId);
        verify(jobOpeningRepository).save(any(JobOpening.class));
    }

    @Test
    void updateJobOpening_shouldThrowBusinessExceptionWhenNotEligible() {
        // Arrange
        JobOpeningUpdateRequest request = new JobOpeningUpdateRequest(
                "Senior Software Engineer",
                "Lead software development",
                "Java, Spring Boot, Microservices");

        EligibilityResponse eligibility = new EligibilityResponse(false, "Campaign is locked");
        when(campaignServiceClient.checkEligibility(campaignId, companyId)).thenReturn(eligibility);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            jobOpeningService.updateJobOpening(campaignId, companyId, jobId, request);
        });

        assertEquals("Campaign is locked", exception.getMessage());
        verify(campaignServiceClient).checkEligibility(campaignId, companyId);
        verify(jobOpeningRepository, never()).findByIdAndCampaignIdAndCompanyId(any(), any(), any());
        verify(jobOpeningRepository, never()).save(any(JobOpening.class));
    }

    @Test
    void updateJobOpening_shouldThrowResourceNotFoundExceptionWhenJobNotFound() {
        // Arrange
        JobOpeningUpdateRequest request = new JobOpeningUpdateRequest(
                "Senior Software Engineer",
                "Lead software development",
                "Java, Spring Boot, Microservices");

        EligibilityResponse eligibility = new EligibilityResponse(true, "");
        when(campaignServiceClient.checkEligibility(campaignId, companyId)).thenReturn(eligibility);
        when(jobOpeningRepository.findByIdAndCampaignIdAndCompanyId(jobId, campaignId, companyId))
                .thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            jobOpeningService.updateJobOpening(campaignId, companyId, jobId, request);
        });

        assertEquals("Job opening not found", exception.getMessage());
        verify(campaignServiceClient).checkEligibility(campaignId, companyId);
        verify(jobOpeningRepository).findByIdAndCampaignIdAndCompanyId(jobId, campaignId, companyId);
        verify(jobOpeningRepository, never()).save(any(JobOpening.class));
    }

    @Test
    void deleteJobOpening_shouldDeleteJobOpeningWhenEligible() {
        // Arrange
        EligibilityResponse eligibility = new EligibilityResponse(true, "");
        when(campaignServiceClient.checkEligibility(campaignId, companyId)).thenReturn(eligibility);
        when(jobOpeningRepository.findByIdAndCampaignIdAndCompanyId(jobId, campaignId, companyId))
                .thenReturn(Optional.of(jobOpening));

        // Act
        jobOpeningService.deleteJobOpening(campaignId, companyId, jobId);

        // Assert
        verify(campaignServiceClient).checkEligibility(campaignId, companyId);
        verify(jobOpeningRepository).findByIdAndCampaignIdAndCompanyId(jobId, campaignId, companyId);
        verify(jobOpeningRepository).delete(jobOpening);
    }

    @Test
    void deleteJobOpening_shouldThrowBusinessExceptionWhenNotEligible() {
        // Arrange
        EligibilityResponse eligibility = new EligibilityResponse(false, "Campaign deadline has passed");
        when(campaignServiceClient.checkEligibility(campaignId, companyId)).thenReturn(eligibility);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            jobOpeningService.deleteJobOpening(campaignId, companyId, jobId);
        });

        assertEquals("Campaign deadline has passed", exception.getMessage());
        verify(campaignServiceClient).checkEligibility(campaignId, companyId);
        verify(jobOpeningRepository, never()).findByIdAndCampaignIdAndCompanyId(any(), any(), any());
        verify(jobOpeningRepository, never()).delete(any(JobOpening.class));
    }

    @Test
    void deleteJobOpening_shouldThrowResourceNotFoundExceptionWhenJobNotFound() {
        // Arrange
        EligibilityResponse eligibility = new EligibilityResponse(true, "");
        when(campaignServiceClient.checkEligibility(campaignId, companyId)).thenReturn(eligibility);
        when(jobOpeningRepository.findByIdAndCampaignIdAndCompanyId(jobId, campaignId, companyId))
                .thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            jobOpeningService.deleteJobOpening(campaignId, companyId, jobId);
        });

        assertEquals("Job opening not found", exception.getMessage());
        verify(campaignServiceClient).checkEligibility(campaignId, companyId);
        verify(jobOpeningRepository).findByIdAndCampaignIdAndCompanyId(jobId, campaignId, companyId);
        verify(jobOpeningRepository, never()).delete(any(JobOpening.class));
    }
}
