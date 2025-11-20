package com.stepin.jobopening.repository;

import com.stepin.jobopening.domain.JobOpening;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryJobOpeningRepository implements JobOpeningRepository {
    private final ConcurrentHashMap<UUID, JobOpening> storage = new ConcurrentHashMap<>();

    @Override
    public JobOpening save(JobOpening jobOpening) {
        storage.put(jobOpening.getId(), jobOpening);
        return jobOpening;
    }

    @Override
    public Optional<JobOpening> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<JobOpening> findByCampaignIdAndCompanyId(UUID campaignId, UUID companyId) {
        return storage.values().stream()
                .filter(job -> job.getCampaignId().equals(campaignId) && job.getCompanyId().equals(companyId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<JobOpening> findByIdAndCampaignIdAndCompanyId(UUID id, UUID campaignId, UUID companyId) {
        return Optional.ofNullable(storage.get(id))
                .filter(job -> job.getCampaignId().equals(campaignId) && job.getCompanyId().equals(companyId));
    }

    @Override
    public void delete(JobOpening jobOpening) {
        storage.remove(jobOpening.getId());
    }
}
