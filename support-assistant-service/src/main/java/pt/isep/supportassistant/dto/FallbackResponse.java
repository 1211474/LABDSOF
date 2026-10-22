package pt.isep.supportassistant.dto;

public record FallbackResponse(
        String error,
        String fallbackUrl,
        boolean isFallback
) {
}
