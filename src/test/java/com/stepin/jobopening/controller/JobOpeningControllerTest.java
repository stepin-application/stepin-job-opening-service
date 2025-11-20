package com.stepin.jobopening.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stepin.jobopening.client.CampaignServiceClient;
import com.stepin.jobopening.dto.EligibilityResponse;
import com.stepin.jobopening.dto.JobOpeningCreateRequest;
import com.stepin.jobopening.dto.JobOpeningUpdateRequest;
import com.stepin.jobopening.exception.GlobalExceptionHandler;
import com.stepin.jobopening.service.JobOpeningService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = { JobOpeningController.class, GlobalExceptionHandler.class })
class JobOpeningControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JobOpeningService jobOpeningService;

    @MockBean
    private CampaignServiceClient campaignServiceClient;

    private UUID campaignId;
    private UUID companyId;
    private UUID jobId;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        campaignId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        companyId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        jobId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        baseUrl = "/campaigns/" + campaignId + "/companies/" + companyId + "/job-openings";
    }

    @Test
    void listJobOpenings_shouldReturnEmptyList() throws Exception {
        // Arrange
        when(jobOpeningService.listJobOpenings(campaignId, companyId))
                .thenReturn(java.util.Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get(baseUrl))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void listJobOpenings_shouldReturnListOfJobOpenings() throws Exception {
        // Arrange
        var response1 = createJobOpeningResponse(jobId, "Software Engineer");
        var response2 = createJobOpeningResponse(UUID.randomUUID(), "DevOps Engineer");

        when(jobOpeningService.listJobOpenings(campaignId, companyId))
                .thenReturn(java.util.Arrays.asList(response1, response2));

        // Act & Assert
        mockMvc.perform(get(baseUrl))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(jobId.toString())))
                .andExpect(jsonPath("$[0].title", is("Software Engineer")))
                .andExpect(jsonPath("$[0].campaignId", is(campaignId.toString())))
                .andExpect(jsonPath("$[0].companyId", is(companyId.toString())))
                .andExpect(jsonPath("$[1].title", is("DevOps Engineer")));
    }

    @Test
    void createJobOpening_shouldReturnCreatedWithValidRequest() throws Exception {
        // Arrange
        JobOpeningCreateRequest request = new JobOpeningCreateRequest(
                "Software Engineer",
                "Develop software",
                "Java, Spring Boot");

        var response = createJobOpeningResponse(jobId, "Software Engineer");

        when(jobOpeningService.createJobOpening(eq(campaignId), eq(companyId), any(JobOpeningCreateRequest.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(jobId.toString())))
                .andExpect(jsonPath("$.title", is("Software Engineer")))
                .andExpect(jsonPath("$.description", is("Develop software")))
                .andExpect(jsonPath("$.requirements", is("Java, Spring Boot")))
                .andExpect(jsonPath("$.campaignId", is(campaignId.toString())))
                .andExpect(jsonPath("$.companyId", is(companyId.toString())))
                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andExpect(jsonPath("$.updatedAt", notNullValue()));
    }

    @Test
    void createJobOpening_shouldReturnBadRequestWhenTitleIsBlank() throws Exception {
        // Arrange
        JobOpeningCreateRequest request = new JobOpeningCreateRequest(
                "",
                "Develop software",
                "Java, Spring Boot");

        // Act & Assert
        mockMvc.perform(post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", containsString("title")))
                .andExpect(jsonPath("$.path", notNullValue()))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void createJobOpening_shouldReturnBadRequestWhenTitleIsNull() throws Exception {
        // Arrange
        JobOpeningCreateRequest request = new JobOpeningCreateRequest(
                null,
                "Develop software",
                "Java, Spring Boot");

        // Act & Assert
        mockMvc.perform(post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", containsString("title")))
                .andExpect(jsonPath("$.path", notNullValue()))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void createJobOpening_shouldReturnConflictWhenCampaignIsLocked() throws Exception {
        // Arrange
        JobOpeningCreateRequest request = new JobOpeningCreateRequest(
                "Software Engineer",
                "Develop software",
                "Java, Spring Boot");

        when(jobOpeningService.createJobOpening(eq(campaignId), eq(companyId), any(JobOpeningCreateRequest.class)))
                .thenThrow(new com.stepin.jobopening.exception.BusinessException("Campaign is locked"));

        // Act & Assert
        mockMvc.perform(post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.message", is("Campaign is locked")))
                .andExpect(jsonPath("$.path", notNullValue()))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void createJobOpening_shouldReturnConflictWhenDeadlinePassed() throws Exception {
        // Arrange
        JobOpeningCreateRequest request = new JobOpeningCreateRequest(
                "Software Engineer",
                "Develop software",
                "Java, Spring Boot");

        when(jobOpeningService.createJobOpening(eq(campaignId), eq(companyId), any(JobOpeningCreateRequest.class)))
                .thenThrow(new com.stepin.jobopening.exception.BusinessException("Campaign deadline has passed"));

        // Act & Assert
        mockMvc.perform(post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.message", is("Campaign deadline has passed")))
                .andExpect(jsonPath("$.path", notNullValue()))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void createJobOpening_shouldReturnConflictWhenInvitationNotAccepted() throws Exception {
        // Arrange
        JobOpeningCreateRequest request = new JobOpeningCreateRequest(
                "Software Engineer",
                "Develop software",
                "Java, Spring Boot");

        when(jobOpeningService.createJobOpening(eq(campaignId), eq(companyId), any(JobOpeningCreateRequest.class)))
                .thenThrow(
                        new com.stepin.jobopening.exception.BusinessException("Company has not accepted invitation"));

        // Act & Assert
        mockMvc.perform(post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.message", is("Company has not accepted invitation")))
                .andExpect(jsonPath("$.path", notNullValue()))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void updateJobOpening_shouldReturnOkWithValidRequest() throws Exception {
        // Arrange
        JobOpeningUpdateRequest request = new JobOpeningUpdateRequest(
                "Senior Software Engineer",
                "Lead software development",
                "Java, Spring Boot, Microservices");

        var response = createJobOpeningResponse(jobId, "Senior Software Engineer");

        when(jobOpeningService.updateJobOpening(eq(campaignId), eq(companyId), eq(jobId),
                any(JobOpeningUpdateRequest.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(put(baseUrl + "/" + jobId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(jobId.toString())))
                .andExpect(jsonPath("$.title", is("Senior Software Engineer")))
                .andExpect(jsonPath("$.campaignId", is(campaignId.toString())))
                .andExpect(jsonPath("$.companyId", is(companyId.toString())));
    }

    @Test
    void updateJobOpening_shouldReturnBadRequestWhenTitleIsBlank() throws Exception {
        // Arrange
        JobOpeningUpdateRequest request = new JobOpeningUpdateRequest(
                "",
                "Lead software development",
                "Java, Spring Boot, Microservices");

        // Act & Assert
        mockMvc.perform(put(baseUrl + "/" + jobId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", containsString("title")))
                .andExpect(jsonPath("$.path", notNullValue()))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void updateJobOpening_shouldReturnNotFoundWhenJobDoesNotExist() throws Exception {
        // Arrange
        JobOpeningUpdateRequest request = new JobOpeningUpdateRequest(
                "Senior Software Engineer",
                "Lead software development",
                "Java, Spring Boot, Microservices");

        when(jobOpeningService.updateJobOpening(eq(campaignId), eq(companyId), eq(jobId),
                any(JobOpeningUpdateRequest.class)))
                .thenThrow(new com.stepin.jobopening.exception.ResourceNotFoundException("Job opening not found"));

        // Act & Assert
        mockMvc.perform(put(baseUrl + "/" + jobId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Job opening not found")))
                .andExpect(jsonPath("$.path", notNullValue()))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void updateJobOpening_shouldReturnConflictWhenCampaignIsLocked() throws Exception {
        // Arrange
        JobOpeningUpdateRequest request = new JobOpeningUpdateRequest(
                "Senior Software Engineer",
                "Lead software development",
                "Java, Spring Boot, Microservices");

        when(jobOpeningService.updateJobOpening(eq(campaignId), eq(companyId), eq(jobId),
                any(JobOpeningUpdateRequest.class)))
                .thenThrow(new com.stepin.jobopening.exception.BusinessException("Campaign is locked"));

        // Act & Assert
        mockMvc.perform(put(baseUrl + "/" + jobId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.message", is("Campaign is locked")))
                .andExpect(jsonPath("$.path", notNullValue()))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void deleteJobOpening_shouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(jobOpeningService).deleteJobOpening(campaignId, companyId, jobId);

        // Act & Assert
        mockMvc.perform(delete(baseUrl + "/" + jobId))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    void deleteJobOpening_shouldReturnNotFoundWhenJobDoesNotExist() throws Exception {
        // Arrange
        doThrow(new com.stepin.jobopening.exception.ResourceNotFoundException("Job opening not found"))
                .when(jobOpeningService).deleteJobOpening(campaignId, companyId, jobId);

        // Act & Assert
        mockMvc.perform(delete(baseUrl + "/" + jobId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Job opening not found")))
                .andExpect(jsonPath("$.path", notNullValue()))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void deleteJobOpening_shouldReturnConflictWhenCampaignIsLocked() throws Exception {
        // Arrange
        doThrow(new com.stepin.jobopening.exception.BusinessException("Campaign is locked"))
                .when(jobOpeningService).deleteJobOpening(campaignId, companyId, jobId);

        // Act & Assert
        mockMvc.perform(delete(baseUrl + "/" + jobId))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.message", is("Campaign is locked")))
                .andExpect(jsonPath("$.path", notNullValue()))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void deleteJobOpening_shouldReturnConflictWhenDeadlinePassed() throws Exception {
        // Arrange
        doThrow(new com.stepin.jobopening.exception.BusinessException("Campaign deadline has passed"))
                .when(jobOpeningService).deleteJobOpening(campaignId, companyId, jobId);

        // Act & Assert
        mockMvc.perform(delete(baseUrl + "/" + jobId))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.message", is("Campaign deadline has passed")))
                .andExpect(jsonPath("$.path", notNullValue()))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    // Helper method to create JobOpeningResponse
    private com.stepin.jobopening.dto.JobOpeningResponse createJobOpeningResponse(UUID id, String title) {
        var response = new com.stepin.jobopening.dto.JobOpeningResponse();
        response.setId(id);
        response.setCampaignId(campaignId);
        response.setCompanyId(companyId);
        response.setTitle(title);
        response.setDescription("Develop software");
        response.setRequirements("Java, Spring Boot");
        response.setCreatedAt(java.time.OffsetDateTime.now());
        response.setUpdatedAt(java.time.OffsetDateTime.now());
        return response;
    }
}
