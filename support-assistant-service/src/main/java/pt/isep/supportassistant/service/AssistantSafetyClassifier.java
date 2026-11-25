package pt.isep.supportassistant.service;

import org.springframework.stereotype.Component;
import pt.isep.supportassistant.dto.RefusalCategory;

import java.util.List;

/**
 * Input guardrail that runs before any directory lookup or model call.
 *
 * <p>Matching is deterministic and accent-insensitive. It deliberately does not treat a plain
 * service keyword such as "farmácias" or "medicamentos" as a medical request.</p>
 */
@Component
public class AssistantSafetyClassifier {

    private static final List<String> EMERGENCY_PATTERNS = List.of(
            "emergencia", "emergency", "112", "sns 24", "sns24", "socorro",
            "ambulancia", "ambulance", "urgencia", "urgente", "urgent",
            "paragem cardiaca", "ataque cardiaco", "heart attack", "dor no peito",
            "chest pain", "desmaio", "convulsao", "nao respira", "not breathing",
            "envenenamento", "poisoning", "suicidio", "suicide"
    );

    private static final List<String> PRESCRIPTION_PATTERNS = List.of(
            "receita", "prescricao", "prescription", "receitar", "prescrever",
            "prescribe", "posologia", "dosagem", "dosage", "antibiotico", "antibiotic",
            "qual medicamento", "que medicamento", "what medicine", "medicamento para",
            "remedio para", "medicine for"
    );

    private static final List<String> MEDICAL_PATTERNS = List.of(
            "diagnostico", "diagnosis", "diagnose", "doenca", "disease", "sintoma",
            "symptom", "tratamento", "treatment", "como tratar", "how to treat",
            "curar", "cure", "aconselhamento medico", "medical advice",
            "conselho medico", "devo ir ao medico", "should i see a doctor"
    );

    public SafetyAssessment assess(String rawQuery) {
        String normalized = QueryText.normalize(rawQuery);

        if (matches(normalized, EMERGENCY_PATTERNS)) {
            return new SafetyAssessment(RefusalCategory.EMERGENCY);
        }
        if (matches(normalized, PRESCRIPTION_PATTERNS)) {
            return new SafetyAssessment(RefusalCategory.PRESCRIPTION);
        }
        if (matches(normalized, MEDICAL_PATTERNS)) {
            return new SafetyAssessment(RefusalCategory.MEDICAL_ADVICE);
        }

        return new SafetyAssessment(null);
    }

    private boolean matches(String normalized, List<String> patterns) {
        for (String pattern : patterns) {
            boolean matched = pattern.contains(" ")
                    ? QueryText.containsPhrase(normalized, pattern)
                    : QueryText.containsKeyword(normalized, pattern);
            if (matched) {
                return true;
            }
        }

        return false;
    }

    public record SafetyAssessment(RefusalCategory category) {

        public boolean isUnsafe() {
            return category != null;
        }
    }
}
