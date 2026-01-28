package com.stepin.jobopening.repository;

import com.stepin.jobopening.domain.JobOpening;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobOpeningRepository extends JpaRepository<JobOpening, UUID> {
    List<JobOpening> findByCampaignIdAndCompanyId(UUID campaignId, UUID companyId);

    List<JobOpening> findByCompanyId(UUID companyId);

    List<JobOpening> findByCampaignId(UUID campaignId);

    Optional<JobOpening> findByIdAndCampaignIdAndCompanyId(UUID id, UUID campaignId, UUID companyId);
}
