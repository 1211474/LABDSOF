package pt.isep.supportassistant.service;

import org.springframework.stereotype.Component;
import pt.isep.supportassistant.dto.GroundedService;

import java.util.List;

/**
 * Builds the Gemini prompt from verified records.
 *
 * <p>This is a stricter allowlist than the API response: names, type, coarse location, hours,
 * status and update timestamp may be sent. Service IDs and telephone numbers never leave the
 * system boundary, and the caregiver's care profile is never part of the prompt.</p>
 */
@Component
public class GroundedAnswerGenerator {

    static final String SYSTEM_PROMPT = """
            You are a service-directory assistant for informal caregivers in Porto.
            Answer in Portuguese, in at most three short sentences.
            Use only the service records supplied in the user message.
            Never invent names, locations, hours, telephone numbers or availability.
            Never give medical advice, diagnoses, prescriptions or emergency instructions.
            Never claim real-time availability: ask the caregiver to confirm by telephone.
            Do not write a source line; the platform adds provenance separately.
            """;

    public LlmPrompt createPrompt(String query, List<GroundedService> services) {
        StringBuilder userPrompt = new StringBuilder("Pergunta: ")
                .append(query == null ? "" : query.strip())
                .append("\n\nRegistos verificados do diretório:\n");

        for (GroundedService service : services) {
            userPrompt.append("- nome: ").append(safe(service.name()))
                    .append("; tipo: ").append(safe(service.type()))
                    .append("; zona: ").append(safe(service.coarseLocation()))
                    .append("; horário: ").append(safe(service.operatingHours()))
                    .append("; estado: ").append(safe(service.currentStatus()))
                    .append("; atualizado: ").append(service.lastUpdated() == null
                            ? "não indicado" : service.lastUpdated().toString())
                    .append('\n');
        }

        return new LlmPrompt(SYSTEM_PROMPT, userPrompt.toString());
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "não indicado" : value;
    }
}
