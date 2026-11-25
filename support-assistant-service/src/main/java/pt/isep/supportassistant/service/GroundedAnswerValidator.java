package pt.isep.supportassistant.service;

import org.springframework.stereotype.Component;
import pt.isep.supportassistant.dto.GroundedService;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Output guardrail. A model answer is only used when it stays inside the grounded records.
 */
@Component
public class GroundedAnswerValidator {

    private static final int MAX_ANSWER_LENGTH = 600;
    private static final Pattern PHONE_LIKE = Pattern.compile("\\+?\\d[\\d\\s-]{8,}");

    private final AssistantSafetyClassifier safetyClassifier;

    public GroundedAnswerValidator(AssistantSafetyClassifier safetyClassifier) {
        this.safetyClassifier = safetyClassifier;
    }

    public boolean isValid(String candidate, List<GroundedService> services) {
        if (candidate == null || candidate.isBlank() || candidate.length() > MAX_ANSWER_LENGTH) {
            return false;
        }
        if (safetyClassifier.assess(candidate).isUnsafe() || candidate.contains("Fonte:")) {
            return false;
        }
        if (PHONE_LIKE.matcher(candidate).find()) {
            return false;
        }
        if (services.isEmpty()) {
            return true;
        }

        String lowerCandidate = candidate.toLowerCase(Locale.ROOT);
        return services.stream()
                .anyMatch(service -> service.name() != null
                        && lowerCandidate.contains(service.name().toLowerCase(Locale.ROOT)));
    }
}
