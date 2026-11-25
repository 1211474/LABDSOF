package pt.isep.supportassistant.service;

import org.junit.jupiter.api.Test;
import pt.isep.supportassistant.dto.GroundedService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GroundedAnswerGeneratorTest {

    private final GroundedAnswerGenerator generator = new GroundedAnswerGenerator();

    @Test
    void includesOnlyOperationalServiceFactsInThePrompt() {
        UUID id = UUID.randomUUID();
        GroundedService service = new GroundedService(
                id, "Farmácia da Boavista", "PHARMACY", "Boavista", "08:00 - 22:00",
                "OPEN", "+351220000001", true,
                Instant.parse("2026-10-12T15:30:00Z"), VerifiedServiceGrounding.SOURCE);

        LlmPrompt prompt = generator.createPrompt("farmácias perto da Boavista", List.of(service));

        assertThat(prompt.userPrompt())
                .contains("farmácias perto da Boavista")
                .contains("Farmácia da Boavista", "PHARMACY", "Boavista", "08:00 - 22:00", "OPEN")
                .contains("2026-10-12T15:30:00Z")
                .doesNotContain(id.toString(), "+351220000001", "organisationId", VerifiedServiceGrounding.SOURCE);
        assertThat(prompt.systemPrompt())
                .contains("Use only the service records")
                .contains("Never give medical advice");
    }

    @Test
    void handlesMissingValuesWithoutLeakingNulls() {
        GroundedService service = new GroundedService(
                UUID.randomUUID(), null, "OTHER", null, null, null, null, true, null, null);

        LlmPrompt prompt = generator.createPrompt(null, List.of(service));

        assertThat(prompt.userPrompt())
                .contains("não indicado")
                .doesNotContain("null");
    }
}
