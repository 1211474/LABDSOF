package pt.isep.supportassistant.dto;

import jakarta.validation.constraints.NotBlank;

public record AiNavigateRequest(
        @NotBlank(message = "query is required")
        String query
) {
}
