package pt.isep.supportassistant.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import pt.isep.supportassistant.dto.AiNavigateResponse;
import pt.isep.supportassistant.dto.CareServiceDto;
import pt.isep.supportassistant.integration.CarePlatformClient;

import java.util.List;
import java.util.Locale;

@Service
public class AiNavigatorService {

    private static final List<String> LIMITATIONS = List.of(
            "O assistente de IA ainda não está ativo nesta fase (Sprint 0).",
            "Os resultados vêm do diretório de serviços verificado e usam uma pesquisa simples por palavras-chave.",
            "A disponibilidade em tempo real não é garantida. Confirme sempre diretamente com a instituição."
    );

    private final CarePlatformClient carePlatformClient;

    public AiNavigatorService(CarePlatformClient carePlatformClient) {
        this.carePlatformClient = carePlatformClient;
    }

    public AiNavigateResponse navigate(String query) {
        String normalizedQuery = query.toLowerCase(Locale.ROOT);
        String type = detectType(normalizedQuery);
        String coarseLocation = detectLocation(normalizedQuery);

        List<CareServiceDto> services;
        try {
            services = carePlatformClient.fetchServices(type, coarseLocation);
        } catch (RestClientException exception) {
            throw new AssistantUnavailableException("Care Platform API is unavailable", exception);
        }

        String answer;
        if (services.isEmpty()) {
            answer = "Não foram encontrados serviços verificados para esta pesquisa. "
                    + "Use a pesquisa do diretório ou contacte diretamente os serviços locais.";
        } else {
            answer = "Foram encontrados " + services.size()
                    + " serviço(s) no diretório verificado. Confirme a disponibilidade diretamente antes de se deslocar.";
        }

        return new AiNavigateResponse(answer, services, true, LIMITATIONS);
    }

    private String detectType(String query) {
        if (query.contains("farmác") || query.contains("pharmac")) {
            return "PHARMACY";
        }
        if (query.contains("centro de dia") || query.contains("day care")) {
            return "DAY_CENTRE";
        }
        if (query.contains("transporte") || query.contains("transport")) {
            return "ADAPTED_TRANSPORT";
        }
        if (query.contains("centro de saúde") || query.contains("health centre")) {
            return "HEALTH_CENTRE";
        }
        if (query.contains("domicílio") || query.contains("home support")) {
            return "HOME_SUPPORT";
        }
        return null;
    }

    private String detectLocation(String query) {
        if (query.contains("boavista")) {
            return "Boavista";
        }
        if (query.contains("campanhã") || query.contains("campanha")) {
            return "Campanhã";
        }
        return null;
    }
}
