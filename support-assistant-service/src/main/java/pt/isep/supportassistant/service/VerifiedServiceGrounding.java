package pt.isep.supportassistant.service;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import pt.isep.supportassistant.dto.CareServiceDto;
import pt.isep.supportassistant.dto.GroundedService;
import pt.isep.supportassistant.dto.SourceContext;
import pt.isep.supportassistant.integration.CarePlatformClient;

import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 * Retrieves the application records that may ground an assistant answer.
 *
 * <p>Only records published by verified organisations and recognised service types are kept.
 * Each record is then projected onto the {@link GroundedService} allowlist before it can be
 * used by the answer builder or returned to the caregiver.</p>
 */
@Component
public class VerifiedServiceGrounding {

    static final String SOURCE = "Diretório de serviços verificado da plataforma";

    private static final Set<String> SUPPORTED_TYPES = Set.of(
            "PHARMACY",
            "DAY_CENTRE",
            "ADAPTED_TRANSPORT",
            "HEALTH_CENTRE",
            "HOME_SUPPORT",
            "OTHER"
    );

    private final CarePlatformClient carePlatformClient;

    public VerifiedServiceGrounding(CarePlatformClient carePlatformClient) {
        this.carePlatformClient = carePlatformClient;
    }

    public GroundingResult retrieve(String type) {
        List<CareServiceDto> services;
        try {
            services = carePlatformClient.fetchServices(type, null);
        } catch (RestClientException exception) {
            throw new AssistantUnavailableException("Care Platform API is unavailable", exception);
        }

        List<GroundedService> records = services.stream()
                .filter(CareServiceDto::verified)
                .filter(service -> SUPPORTED_TYPES.contains(service.type()))
                .map(this::ground)
                .toList();

        SourceContext sourceContext = new SourceContext(
                SOURCE,
                Instant.now(),
                GroundedService.ALLOWED_FIELDS,
                records.size()
        );

        return new GroundingResult(records, sourceContext);
    }

    private GroundedService ground(CareServiceDto service) {
        return new GroundedService(
                service.id(),
                service.name(),
                service.type(),
                service.coarseLocation(),
                service.operatingHours(),
                service.currentStatus(),
                service.contactPhone(),
                true,
                service.lastUpdated(),
                SOURCE
        );
    }

    public record GroundingResult(List<GroundedService> records, SourceContext sourceContext) {
    }
}
