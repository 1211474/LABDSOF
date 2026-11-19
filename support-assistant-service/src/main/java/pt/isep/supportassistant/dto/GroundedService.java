package pt.isep.supportassistant.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * A service fact that is safe to use in an assistant answer.
 *
 * <p>This record is the explicit allowlist of fields that leave the grounding layer. Internal
 * application fields such as {@code organisationId} are deliberately absent.</p>
 */
public record GroundedService(
        UUID id,
        String name,
        String type,
        String coarseLocation,
        String operatingHours,
        String currentStatus,
        String contactPhone,
        boolean verified,
        Instant lastUpdated,
        String source
) {

    public static final List<String> ALLOWED_FIELDS = List.of(
            "id",
            "name",
            "type",
            "coarseLocation",
            "operatingHours",
            "currentStatus",
            "contactPhone",
            "verified",
            "lastUpdated",
            "source"
    );
}
