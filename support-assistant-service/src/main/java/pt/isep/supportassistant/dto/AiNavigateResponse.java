package pt.isep.supportassistant.dto;

import java.util.List;

public record AiNavigateResponse(
        String answer,
        List<CareServiceDto> services,
        boolean isFallback,
        List<String> limitations
) {
}
