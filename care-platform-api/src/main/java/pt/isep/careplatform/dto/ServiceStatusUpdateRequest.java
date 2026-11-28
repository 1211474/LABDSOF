package pt.isep.careplatform.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pt.isep.careplatform.entity.ServiceStatus;

public record ServiceStatusUpdateRequest(
        @NotNull ServiceStatus status,
        @Size(max = 120) String operatingHours,
        @Size(max = 500) String officialNotice
) {
}
