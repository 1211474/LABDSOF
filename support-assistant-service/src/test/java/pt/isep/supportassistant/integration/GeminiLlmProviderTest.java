package pt.isep.supportassistant.integration;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import pt.isep.supportassistant.config.LlmProperties;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GeminiLlmProviderTest {

    private final AtomicReference<String> capturedBody = new AtomicReference<>();
    private final AtomicReference<String> capturedKey = new AtomicReference<>();
    private HttpServer server;

    @AfterEach
    void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void sendsSystemAndUserPromptsWithApiKeyHeader() throws IOException {
        startServer(200, "{\"candidates\":[{\"content\":{\"role\":\"model\","
                + "\"parts\":[{\"text\":\"Encontrei a Farmácia da Boavista.\"}]},"
                + "\"finishReason\":\"STOP\"}]}");

        String answer = provider().complete("system rules", "Pergunta sobre serviços");

        assertThat(answer).isEqualTo("Encontrei a Farmácia da Boavista.");
        assertThat(capturedKey.get()).isEqualTo("test-key");
        assertThat(capturedBody.get())
                .contains("\"systemInstruction\"", "system rules")
                .contains("\"role\":\"user\"", "Pergunta sobre serviços");
    }

    @Test
    void wrapsProviderErrors() throws IOException {
        startServer(500, "{\"error\":{\"message\":\"provider down\"}}");

        assertThatThrownBy(() -> provider().complete("system", "user"))
                .isInstanceOf(LlmCompletionException.class)
                .hasMessageContaining("Gemini request failed");
    }

    @Test
    void rejectsResponsesWithoutCandidates() throws IOException {
        startServer(200, "{\"candidates\":[]}");

        assertThatThrownBy(() -> provider().complete("system", "user"))
                .isInstanceOf(LlmCompletionException.class)
                .hasMessageContaining("no candidates");
    }

    @Test
    void rejectsCandidatesWithoutContent() throws IOException {
        startServer(200, "{\"candidates\":[{}]}");

        assertThatThrownBy(() -> provider().complete("system", "user"))
                .isInstanceOf(LlmCompletionException.class)
                .hasMessageContaining("no content");
    }

    @Test
    void rejectsBlankText() throws IOException {
        startServer(200, "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"   \"}]}}]}");

        assertThatThrownBy(() -> provider().complete("system", "user"))
                .isInstanceOf(LlmCompletionException.class)
                .hasMessageContaining("empty answer");
    }

    @Test
    void isDisabledWithoutAnApiKey() {
        LlmProperties properties = new LlmProperties(
                true, "http://localhost", "", "gemini-2.5-flash", 1000);

        assertThat(new GeminiLlmProvider(null, properties).isEnabled()).isFalse();
    }

    @Test
    void isDisabledWhenSwitchedOff() {
        LlmProperties properties = new LlmProperties(
                false, "http://localhost", "test-key", "gemini-2.5-flash", 1000);

        assertThat(new GeminiLlmProvider(null, properties).isEnabled()).isFalse();
    }

    @Test
    void isEnabledWithKeyAndSwitchOn() {
        LlmProperties properties = new LlmProperties(
                true, "http://localhost", "test-key", "gemini-2.5-flash", 1000);

        assertThat(new GeminiLlmProvider(null, properties).isEnabled()).isTrue();
    }

    private void startServer(int status, String responseJson) throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/v1beta/models/gemini-2.5-flash:generateContent", exchange -> {
            capturedBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            capturedKey.set(exchange.getRequestHeaders().getFirst("x-goog-api-key"));

            byte[] payload = responseJson.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, payload.length);
            try (OutputStream output = exchange.getResponseBody()) {
                output.write(payload);
            }
        });
        server.start();
    }

    private GeminiLlmProvider provider() {
        LlmProperties properties = new LlmProperties(
                true, "http://127.0.0.1:" + server.getAddress().getPort(),
                "test-key", "gemini-2.5-flash", 2000);

        return new GeminiLlmProvider(
                RestClient.builder().baseUrl(properties.baseUrl()).build(), properties);
    }
}
