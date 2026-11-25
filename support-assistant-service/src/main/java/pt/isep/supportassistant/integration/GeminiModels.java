package pt.isep.supportassistant.integration;

import java.util.List;

/**
 * Wire types for the Gemini {@code generateContent} REST endpoint.
 */
public final class GeminiModels {

    private GeminiModels() {
    }

    public record GeminiGenerateRequest(
            GeminiSystemInstruction systemInstruction,
            List<GeminiContent> contents,
            GeminiGenerationConfig generationConfig
    ) {
    }

    public record GeminiSystemInstruction(List<GeminiPart> parts) {
    }

    public record GeminiContent(String role, List<GeminiPart> parts) {
    }

    public record GeminiPart(String text) {
    }

    public record GeminiGenerationConfig(double temperature, int maxOutputTokens) {
    }

    public record GeminiGenerateResponse(List<GeminiCandidate> candidates) {
    }

    public record GeminiCandidate(GeminiContent content, String finishReason) {
    }
}
