package com.stepin.jobopening.mapper;

import com.stepin.jobopening.domain.JobOpening;
import com.stepin.jobopening.dto.JobOpeningCreateRequest;
import com.stepin.jobopening.dto.JobOpeningResponse;
import com.stepin.jobopening.dto.JobOpeningUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * MapStruct mapper for converting between JobOpening domain objects and DTOs.
 */
@Mapper
public interface JobOpeningMapper {

    JobOpeningMapper INSTANCE = Mappers.getMapper(JobOpeningMapper.class);

    /**
     * Converts a JobOpening domain object to a JobOpeningResponse DTO.
     *
     * @param jobOpening the JobOpening domain object
     * @return the JobOpeningResponse DTO
     */
    JobOpeningResponse toResponse(JobOpening jobOpening);

    /**
     * Converts a JobOpeningCreateRequest DTO to a JobOpening domain object.
     * Note: id, campaignId, companyId, createdAt, and updatedAt should be set by the service layer.
     *
     * @param request the JobOpeningCreateRequest DTO
     * @return the JobOpening domain object
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "campaignId", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    JobOpening toEntity(JobOpeningCreateRequest request);

    /**
     * Updates an existing JobOpening entity from a JobOpeningUpdateRequest DTO.
     * Only updates mutable fields (title, description, requirements).
     * Note: updatedAt should be set by the service layer.
     *
     * @param request the JobOpeningUpdateRequest DTO
     * @param jobOpening the existing JobOpening entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "campaignId", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(JobOpeningUpdateRequest request, @MappingTarget JobOpening jobOpening);
}
