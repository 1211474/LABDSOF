package pt.isep.supportassistant.service;

import org.springframework.stereotype.Service;
import pt.isep.supportassistant.dto.AiNavigateResponse;
import pt.isep.supportassistant.dto.GroundedService;
import pt.isep.supportassistant.service.VerifiedServiceGrounding.GroundingResult;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class AiNavigatorService {

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

    public AiNavigatorService(VerifiedServiceGrounding grounding, AssistantQueryParser queryParser) {
        this.grounding = grounding;
        this.queryParser = queryParser;
    }

    public AiNavigateResponse navigate(String query) {
        AssistantQuery assistantQuery = queryParser.parse(query);

        GroundingResult groundingResult = grounding.retrieve(assistantQuery.type());
        List<GroundedService> groundedServices = groundingResult.records();

        List<GroundedService> results = filterAndRank(groundedServices, assistantQuery);
        String location = resolveLocationName(assistantQuery.location(), groundedServices);
        String answer = buildAnswer(assistantQuery, location, results);

        return new AiNavigateResponse(answer, results, true, LIMITATIONS, groundingResult.sourceContext());
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
        answer.append(" Fonte: ").append(VerifiedServiceGrounding.SOURCE).append('.');
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
