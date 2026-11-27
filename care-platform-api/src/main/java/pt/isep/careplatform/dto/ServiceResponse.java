package pt.isep.careplatform.dto;

import pt.isep.careplatform.entity.ServiceStatus;
import pt.isep.careplatform.entity.ServiceType;
import pt.isep.careplatform.entity.SupportService;

import java.time.Instant;
import java.util.UUID;

public record ServiceResponse(
        UUID id,
        UUID organisationId,
        String name,
        ServiceType type,
        String coarseLocation,
        String operatingHours,
        ServiceStatus currentStatus,
        String contactPhone,
        String source,
        boolean verified,
        Instant lastUpdated,
        String organisationEmail,
        String organisationPhone,
        String organisationWebsite,
        String organisationCoarseLocation
) {

    public static ServiceResponse from(SupportService service) {
        var organisation = service.getOrganisation();
        boolean verified = organisation.isVerified();

        return new ServiceResponse(
                service.getId(),
                organisation.getId(),
                service.getName(),
                service.getType(),
                service.getCoarseLocation(),
                service.getOperatingHours(),
                service.getCurrentStatus(),
                service.getContactPhone(),
                organisation.getName(),
                verified,
                service.getLastUpdated(),
                verified ? organisation.getOfficialEmail() : null,
                verified ? organisation.getOfficialPhone() : null,
                verified ? organisation.getWebsite() : null,
                verified ? organisation.getCoarseLocation() : null
        );
    }
}
