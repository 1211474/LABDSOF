package pt.isep.supportassistant.service;

import org.junit.jupiter.api.Test;
import pt.isep.supportassistant.dto.GroundedService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GroundedAnswerValidatorTest {

    private final AssistantSafetyClassifier classifier = new AssistantSafetyClassifier();
    private final GroundedAnswerValidator validator = new GroundedAnswerValidator(classifier);

    private final GroundedService service = new GroundedService(
            UUID.randomUUID(), "Farmácia da Boavista", "PHARMACY", "Boavista", "08:00 - 22:00",
            "OPEN", "+351220000001", true,
            Instant.parse("2026-10-12T15:30:00Z"), VerifiedServiceGrounding.SOURCE);

    @Test
    void acceptsGroundedAnswer() {
        assertThat(validator.isValid(
                "A Farmácia da Boavista está aberta. Confirme a disponibilidade por telefone.",
                List.of(service))).isTrue();
    }

    @Test
    void rejectsAnswerWithoutGroundedService() {
        assertThat(validator.isValid("Encontrei várias opções excelentes.", List.of(service))).isFalse();
    }

    @Test
    void rejectsAnswerWithInventedPhoneNumber() {
        assertThat(validator.isValid(
                "Farmácia da Boavista. Ligue para +351 912 345 678.", List.of(service))).isFalse();
    }

    @Test
    void rejectsAnswerWithMedicalAdvice() {
        assertThat(validator.isValid(
                "Farmácia da Boavista. Tome este medicamento para a sua doença.", List.of(service))).isFalse();
    }

    @Test
    void rejectsAnswerWithItsOwnSourceLine() {
        assertThat(validator.isValid(
                "Farmácia da Boavista. Fonte: inventada.", List.of(service))).isFalse();
    }

    @Test
    void rejectsBlankAndOverlongAnswers() {
        assertThat(validator.isValid("   ", List.of(service))).isFalse();
        assertThat(validator.isValid("Farmácia da Boavista ".repeat(50), List.of(service))).isFalse();
    }

    @Test
    void acceptsShortAnswerWhenThereAreNoServices() {
        assertThat(validator.isValid("Não encontrei resultados.", List.of())).isTrue();
    }
}
