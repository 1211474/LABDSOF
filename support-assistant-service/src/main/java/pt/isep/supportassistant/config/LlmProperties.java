package pt.isep.supportassistant.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration for the managed Gemini provider. No credentials are required for the guardrail
 * and deterministic navigation paths.
 */
@ConfigurationProperties(prefix = "assistant.llm")
public record LlmProperties(
        boolean enabled,
        String baseUrl,
        String apiKey,
        String model,
        int timeoutMs
) {
}
