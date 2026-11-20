package pt.isep.careplatform.dto;

import pt.isep.careplatform.entity.ServiceStatus;
import pt.isep.careplatform.entity.ServiceType;
import pt.isep.careplatform.entity.SupportService;

import java.time.Instant;
import java.util.UUID;

public record ServiceResponse(
        UUID id,
        UUID organisationId,
        String source,
        String name,
        ServiceType type,
        String coarseLocation,
        String operatingHours,
        ServiceStatus currentStatus,
        String contactPhone,
        boolean verified,
        Instant lastUpdated
) {

    public static ServiceResponse from(SupportService service) {
        return new ServiceResponse(
                service.getId(),
                service.getOrganisation().getId(),
                service.getOrganisation().getName(),
                service.getName(),
                service.getType(),
                service.getCoarseLocation(),
                service.getOperatingHours(),
                service.getCurrentStatus(),
                service.getContactPhone(),
                service.isVerified(),
                service.getLastUpdated()
        );
    }
}
