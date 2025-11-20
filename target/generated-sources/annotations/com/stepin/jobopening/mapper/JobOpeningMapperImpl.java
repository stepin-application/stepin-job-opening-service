package com.stepin.jobopening.mapper;

import com.stepin.jobopening.domain.JobOpening;
import com.stepin.jobopening.dto.JobOpeningCreateRequest;
import com.stepin.jobopening.dto.JobOpeningResponse;
import com.stepin.jobopening.dto.JobOpeningUpdateRequest;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-20T12:13:02+0100",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251023-0518, environment: Java 21.0.8 (Eclipse Adoptium)"
)
public class JobOpeningMapperImpl implements JobOpeningMapper {

    @Override
    public JobOpeningResponse toResponse(JobOpening jobOpening) {
        if ( jobOpening == null ) {
            return null;
        }

        JobOpeningResponse jobOpeningResponse = new JobOpeningResponse();

        jobOpeningResponse.setId( jobOpening.getId() );
        jobOpeningResponse.setCampaignId( jobOpening.getCampaignId() );
        jobOpeningResponse.setCompanyId( jobOpening.getCompanyId() );
        jobOpeningResponse.setTitle( jobOpening.getTitle() );
        jobOpeningResponse.setDescription( jobOpening.getDescription() );
        jobOpeningResponse.setRequirements( jobOpening.getRequirements() );
        jobOpeningResponse.setCreatedAt( jobOpening.getCreatedAt() );
        jobOpeningResponse.setUpdatedAt( jobOpening.getUpdatedAt() );

        return jobOpeningResponse;
    }

    @Override
    public JobOpening toEntity(JobOpeningCreateRequest request) {
        if ( request == null ) {
            return null;
        }

        JobOpening jobOpening = new JobOpening();

        jobOpening.setTitle( request.getTitle() );
        jobOpening.setDescription( request.getDescription() );
        jobOpening.setRequirements( request.getRequirements() );

        return jobOpening;
    }

    @Override
    public void updateEntity(JobOpeningUpdateRequest request, JobOpening jobOpening) {
        if ( request == null ) {
            return;
        }

        jobOpening.setTitle( request.getTitle() );
        jobOpening.setDescription( request.getDescription() );
        jobOpening.setRequirements( request.getRequirements() );
    }
}
