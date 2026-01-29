package com.stepin.jobopening.controller;

import com.stepin.jobopening.dto.JobOpeningResponse;
import com.stepin.jobopening.dto.JobOpeningUpdateRequest;
import com.stepin.jobopening.service.JobOpeningService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/job-openings")
public class JobOpeningQueryController {

    private final JobOpeningService jobOpeningService;

    public JobOpeningQueryController(JobOpeningService jobOpeningService) {
        this.jobOpeningService = jobOpeningService;
    }

    @GetMapping("/{jobId}")
    public JobOpeningResponse getJobOpening(@PathVariable UUID jobId) {
        return jobOpeningService.getJobOpening(jobId);
    }

    @PutMapping("/{jobId}")
    public JobOpeningResponse updateJobOpening(
            @PathVariable UUID jobId,
            @Valid @RequestBody JobOpeningUpdateRequest request) {
        return jobOpeningService.updateJobOpeningById(jobId, request);
    }

    @DeleteMapping("/{jobId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteJobOpening(@PathVariable UUID jobId) {
        jobOpeningService.deleteJobOpeningById(jobId);
    }
}
