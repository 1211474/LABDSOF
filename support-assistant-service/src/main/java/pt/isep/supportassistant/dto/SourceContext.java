package pt.isep.supportassistant.dto;

import java.time.Instant;
import java.util.List;

/**
 * Provenance for the records used to build an assistant answer.
 */
public record SourceContext(
        String source,
        Instant retrievedAt,
        List<String> allowedFields,
        int recordCount
) {
}
