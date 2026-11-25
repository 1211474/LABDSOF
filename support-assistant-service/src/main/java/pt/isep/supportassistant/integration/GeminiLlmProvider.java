package pt.isep.supportassistant.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import pt.isep.supportassistant.config.LlmProperties;
import pt.isep.supportassistant.integration.GeminiModels.GeminiCandidate;
import pt.isep.supportassistant.integration.GeminiModels.GeminiContent;
import pt.isep.supportassistant.integration.GeminiModels.GeminiGenerateRequest;
import pt.isep.supportassistant.integration.GeminiModels.GeminiGenerateResponse;
import pt.isep.supportassistant.integration.GeminiModels.GeminiGenerationConfig;
import pt.isep.supportassistant.integration.GeminiModels.GeminiPart;
import pt.isep.supportassistant.integration.GeminiModels.GeminiSystemInstruction;

import java.util.List;

/**
 * Calls Google AI Studio's Gemini API. The API key is sent as a header and is never logged.
 */
@Component
public class GeminiLlmProvider implements LlmProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeminiLlmProvider.class);
    private static final String API_KEY_HEADER = "x-goog-api-key";

    private final RestClient llmRestClient;
    private final LlmProperties properties;

    public GeminiLlmProvider(RestClient llmRestClient, LlmProperties properties) {
        this.llmRestClient = llmRestClient;
        this.properties = properties;
    }

    @Override
    public boolean isEnabled() {
        return properties.enabled() && !properties.apiKey().isBlank();
    }

    @Override
    public String complete(String systemPrompt, String userPrompt) {
        GeminiGenerateRequest request = new GeminiGenerateRequest(
                new GeminiSystemInstruction(List.of(new GeminiPart(systemPrompt))),
                List.of(new GeminiContent("user", List.of(new GeminiPart(userPrompt)))),
                new GeminiGenerationConfig(0.2, 1024)
        );

        GeminiGenerateResponse response;
        try {
            response = llmRestClient.post()
                    .uri("/v1beta/models/" + properties.model() + ":generateContent")
                    .header(API_KEY_HEADER, properties.apiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(GeminiGenerateResponse.class);
        } catch (RestClientException exception) {
            LOGGER.warn("Gemini request failed: {}", exception.getMessage());
            throw new LlmCompletionException("Gemini request failed", exception);
        }

        String text = extractText(response);
        if (text.isBlank()) {
            throw new LlmCompletionException("Gemini returned an empty answer");
        }

        return text;
    }

    private String extractText(GeminiGenerateResponse response) {
        if (response == null || response.candidates() == null || response.candidates().isEmpty()) {
            throw new LlmCompletionException("Gemini returned no candidates");
        }

        GeminiCandidate candidate = response.candidates().getFirst();
        if (candidate.content() == null || candidate.content().parts() == null
                || candidate.content().parts().isEmpty()) {
            throw new LlmCompletionException("Gemini candidate had no content");
        }

        return candidate.content().parts().getFirst().text() == null
                ? "" : candidate.content().parts().getFirst().text().trim();
    }
}
