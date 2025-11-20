package com.stepin.jobopening.repository;

import com.stepin.jobopening.domain.JobOpening;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobOpeningRepository {
    JobOpening save(JobOpening jobOpening);

    Optional<JobOpening> findById(UUID id);

    List<JobOpening> findByCampaignIdAndCompanyId(UUID campaignId, UUID companyId);

    Optional<JobOpening> findByIdAndCampaignIdAndCompanyId(UUID id, UUID campaignId, UUID companyId);

    void delete(JobOpening jobOpening);
}
