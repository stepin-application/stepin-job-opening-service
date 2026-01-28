package com.stepin.jobopening.controller;

import com.stepin.jobopening.dto.JobOpeningResponse;
import com.stepin.jobopening.service.JobOpeningService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/companies/{companyId}/job-openings")
public class CompanyJobOpeningController {

    private final JobOpeningService jobOpeningService;

    public CompanyJobOpeningController(JobOpeningService jobOpeningService) {
        this.jobOpeningService = jobOpeningService;
    }

    @GetMapping
    public List<JobOpeningResponse> listCompanyJobOpenings(@PathVariable UUID companyId) {
        return jobOpeningService.listJobOpeningsByCompany(companyId);
    }
}
