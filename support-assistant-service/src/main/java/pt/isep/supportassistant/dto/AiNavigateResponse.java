package pt.isep.supportassistant.dto;

import java.util.List;

public record AiNavigateResponse(
        String answer,
        List<GroundedService> services,
        boolean isFallback,
        List<String> limitations,
        SourceContext sourceContext
) {
}
