package pt.isep.supportassistant.dto;

import java.time.Instant;
import java.util.UUID;

public record CareServiceDto(
        UUID id,
        UUID organisationId,
        String name,
        String type,
        String coarseLocation,
        String operatingHours,
        String currentStatus,
        String contactPhone,
        boolean verified,
        Instant lastUpdated
) {
}
