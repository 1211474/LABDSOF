package pt.isep.supportassistant.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pt.isep.supportassistant.dto.AiNavigateResponse;
import pt.isep.supportassistant.dto.GroundedService;
import pt.isep.supportassistant.dto.Refusal;
import pt.isep.supportassistant.dto.RefusalContact;
import pt.isep.supportassistant.dto.SourceContext;
import pt.isep.supportassistant.integration.LlmCompletionException;
import pt.isep.supportassistant.integration.LlmProvider;
import pt.isep.supportassistant.service.AssistantSafetyClassifier.SafetyAssessment;
import pt.isep.supportassistant.service.VerifiedServiceGrounding.GroundingResult;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class AiNavigatorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AiNavigatorService.class);
    private static final int MAX_DETAILED_RESULTS = 3;

    private static final List<String> LIMITATIONS = List.of(
            "Resposta construída apenas a partir de registos verificados do diretório de serviços da plataforma.",
            "A disponibilidade em tempo real não é garantida. Confirme sempre diretamente com a instituição.",
            "O assistente não substitui aconselhamento médico, diagnóstico, prescrição ou serviços de emergência (112 / SNS 24)."
    );

    private static final Map<String, String> TYPE_PLURALS = Map.of(
            "PHARMACY", "farmácias",
            "DAY_CENTRE", "centros de dia",
            "ADAPTED_TRANSPORT", "serviços de transporte adaptado",
            "HEALTH_CENTRE", "centros de saúde",
            "HOME_SUPPORT", "serviços de apoio ao domicílio",
            "OTHER", "serviços"
    );

    private final VerifiedServiceGrounding grounding;
    private final AssistantQueryParser queryParser;
    private final AssistantSafetyClassifier safetyClassifier;
    private final RefusalFactory refusalFactory;
    private final GroundedAnswerGenerator answerGenerator;
    private final GroundedAnswerValidator answerValidator;
    private final LlmProvider llmProvider;

    public AiNavigatorService(
            VerifiedServiceGrounding grounding,
            AssistantQueryParser queryParser,
            AssistantSafetyClassifier safetyClassifier,
            RefusalFactory refusalFactory,
            GroundedAnswerGenerator answerGenerator,
            GroundedAnswerValidator answerValidator,
            LlmProvider llmProvider
    ) {
        this.grounding = grounding;
        this.queryParser = queryParser;
        this.safetyClassifier = safetyClassifier;
        this.refusalFactory = refusalFactory;
        this.answerGenerator = answerGenerator;
        this.answerValidator = answerValidator;
        this.llmProvider = llmProvider;
    }

    public AiNavigateResponse navigate(String query) {
        SafetyAssessment safety = safetyClassifier.assess(query);
        if (safety.isUnsafe()) {
            return refuse(safety);
        }

        AssistantQuery assistantQuery = queryParser.parse(query);

        GroundingResult groundingResult = grounding.retrieve(assistantQuery.type());
        List<GroundedService> groundedServices = groundingResult.records();

        List<GroundedService> results = filterAndRank(groundedServices, assistantQuery);
        String location = resolveLocationName(assistantQuery.location(), groundedServices);
        String deterministicAnswer = buildAnswer(assistantQuery, location, results);

        if (llmProvider.isEnabled() && !results.isEmpty()) {
            LlmPrompt prompt = answerGenerator.createPrompt(query, results);
            try {
                String candidate = llmProvider.complete(prompt.systemPrompt(), prompt.userPrompt());
                if (answerValidator.isValid(candidate, results)) {
                    StringBuilder answer = new StringBuilder(candidate);
                    appendSource(answer, groundingResult.sourceContext().source());
                    return new AiNavigateResponse(
                            answer.toString(), results, false, LIMITATIONS,
                            groundingResult.sourceContext(), null);
                }
                LOGGER.warn("Gemini answer failed output validation; using the grounded deterministic answer");
            } catch (LlmCompletionException exception) {
                LOGGER.warn("Gemini was unavailable; using the grounded deterministic answer");
            }
        }

        return new AiNavigateResponse(
                deterministicAnswer, results, true, LIMITATIONS, groundingResult.sourceContext(), null);
    }

    private AiNavigateResponse refuse(SafetyAssessment safety) {
        Refusal refusal = refusalFactory.create(safety.category());
        StringBuilder answer = new StringBuilder(refusal.reason()).append(" Contactos: ");

        for (int index = 0; index < refusal.contacts().size(); index++) {
            if (index > 0) {
                answer.append("; ");
            }
            RefusalContact contact = refusal.contacts().get(index);
            answer.append(contact.label()).append(' ').append(contact.phoneNumber());
        }

        appendSource(answer, RefusalFactory.REFUSAL_SOURCE);

        SourceContext sourceContext = new SourceContext(
                RefusalFactory.REFUSAL_SOURCE, Instant.now(), List.of(), 0);

        return new AiNavigateResponse(answer.toString(), List.of(), false, LIMITATIONS, sourceContext, refusal);
    }

    private List<GroundedService> filterAndRank(List<GroundedService> services, AssistantQuery assistantQuery) {
        String requestedLocation = QueryText.normalize(assistantQuery.location());

        return services.stream()
                .filter(GroundedService::verified)
                .filter(service -> requestedLocation.isEmpty()
                        || requestedLocation.equals(QueryText.normalize(service.coarseLocation())))
                .filter(service -> matchesStatus(service, assistantQuery.status()))
                .sorted(Comparator
                        .comparingInt((GroundedService service) -> statusRank(service.currentStatus()))
                        .thenComparing(GroundedService::name, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();
    }

    private boolean matchesStatus(GroundedService service, AssistantQuery.StatusPreference status) {
        return switch (status) {
            case OPEN_ONLY -> "OPEN".equals(service.currentStatus());
            case CLOSED_ONLY -> "CLOSED".equals(service.currentStatus())
                    || "TEMPORARILY_CLOSED".equals(service.currentStatus());
            case ANY -> true;
        };
    }

    private int statusRank(String status) {
        if (status == null) {
            return 3;
        }

        return switch (status) {
            case "OPEN" -> 0;
            case "TEMPORARILY_CLOSED" -> 1;
            case "CLOSED" -> 2;
            default -> 3;
        };
    }

    private String resolveLocationName(String requestedLocation, List<GroundedService> services) {
        if (requestedLocation == null || requestedLocation.isBlank()) {
            return null;
        }

        String normalizedLocation = QueryText.normalize(requestedLocation);
        return services.stream()
                .map(GroundedService::coarseLocation)
                .filter(Objects::nonNull)
                .filter(location -> QueryText.normalize(location).equals(normalizedLocation))
                .findFirst()
                .orElse(requestedLocation);
    }

    private String buildAnswer(AssistantQuery assistantQuery, String location, List<GroundedService> results) {
        String subject = assistantQuery.type() == null ? null : TYPE_PLURALS.get(assistantQuery.type());
        StringBuilder answer = new StringBuilder();

        if (results.isEmpty()) {
            answer.append("Não encontrei ")
                    .append(subject == null ? "serviços" : subject)
                    .append(statusClause(assistantQuery.status()));
            appendLocation(answer, location);
            answer.append(" no diretório da plataforma. ")
                    .append("Tente outra zona ou tipo de serviço, ou contacte diretamente os serviços locais.");
            appendSource(answer);
            return answer.toString();
        }

        answer.append("Encontrei ")
                .append(results.size())
                .append(results.size() == 1 ? " resultado" : " resultados");

        if (subject != null) {
            answer.append(" para ").append(subject);
        }

        answer.append(statusClause(assistantQuery.status()));
        appendLocation(answer, location);
        answer.append(" no diretório da plataforma.");

        if (results.size() <= MAX_DETAILED_RESULTS) {
            appendDetailedResults(answer, results);
        } else {
            answer.append(" A lista completa está apresentada abaixo.");
        }

        answer.append(" Confirme sempre a disponibilidade por telefone antes de se deslocar.");
        appendSource(answer);

        return answer.toString();
    }

    private void appendDetailedResults(StringBuilder answer, List<GroundedService> results) {
        answer.append(' ');

        for (int index = 0; index < results.size(); index++) {
            if (index > 0) {
                answer.append("; ");
            }

            GroundedService service = results.get(index);
            answer.append(service.name())
                    .append(" (").append(service.coarseLocation())
                    .append("; estado: ").append(statusLabel(service.currentStatus()))
                    .append("; horário: ").append(service.operatingHours())
                    .append("; telefone: ").append(service.contactPhone())
                    .append(")");
        }

        answer.append('.');
    }

    private void appendLocation(StringBuilder answer, String location) {
        if (location != null && !location.isBlank()) {
            answer.append(" perto de ").append(location);
        }
    }

    private void appendSource(StringBuilder answer) {
        appendSource(answer, VerifiedServiceGrounding.SOURCE);
    }

    private void appendSource(StringBuilder answer, String source) {
        answer.append(" Fonte: ").append(source).append('.');
    }

    private String statusClause(AssistantQuery.StatusPreference status) {
        return switch (status) {
            case OPEN_ONLY -> " com estado aberto";
            case CLOSED_ONLY -> " com estado fechado";
            case ANY -> "";
        };
    }

    private String statusLabel(String status) {
        if (status == null) {
            return "desconhecido";
        }

        return switch (status) {
            case "OPEN" -> "aberto";
            case "CLOSED" -> "fechado";
            case "TEMPORARILY_CLOSED" -> "temporariamente fechado";
            default -> "desconhecido";
        };
    }
}
