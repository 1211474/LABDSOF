package pt.isep.supportassistant.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.ResourceAccessException;
import pt.isep.supportassistant.dto.CareServiceDto;
import pt.isep.supportassistant.dto.GroundedService;
import pt.isep.supportassistant.integration.CarePlatformClient;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerifiedServiceGroundingTest {

    @Mock
    private CarePlatformClient carePlatformClient;

    private VerifiedServiceGrounding grounding;

    @BeforeEach
    void setUp() {
        grounding = new VerifiedServiceGrounding(carePlatformClient);
    }

    @Test
    void retrievesOnlyVerifiedSupportedRecordsWithSourceContext() {
        CareServiceDto verified = service("Farmácia da Boavista", "PHARMACY", true);
        CareServiceDto unverified = service("Farmácia Não Verificada", "PHARMACY", false);
        CareServiceDto unsupported = service("Serviço Desconhecido", "MYSTERY_SERVICE", true);

        when(carePlatformClient.fetchServices(eq("PHARMACY"), isNull()))
                .thenReturn(List.of(verified, unverified, unsupported));

        VerifiedServiceGrounding.GroundingResult result = grounding.retrieve("PHARMACY");

        verify(carePlatformClient).fetchServices("PHARMACY", null);
        assertThat(result.records()).hasSize(1);
        GroundedService record = result.records().getFirst();
        assertThat(record.id()).isEqualTo(verified.id());
        assertThat(record.name()).isEqualTo(verified.name());
        assertThat(record.type()).isEqualTo(verified.type());
        assertThat(record.coarseLocation()).isEqualTo(verified.coarseLocation());
        assertThat(record.operatingHours()).isEqualTo(verified.operatingHours());
        assertThat(record.currentStatus()).isEqualTo(verified.currentStatus());
        assertThat(record.contactPhone()).isEqualTo(verified.contactPhone());
        assertThat(record.verified()).isTrue();
        assertThat(record.lastUpdated()).isEqualTo(verified.lastUpdated());
        assertThat(record.source()).isEqualTo(VerifiedServiceGrounding.SOURCE);

        assertThat(result.sourceContext().source()).isEqualTo(VerifiedServiceGrounding.SOURCE);
        assertThat(result.sourceContext().retrievedAt()).isNotNull();
        assertThat(result.sourceContext().allowedFields())
                .containsExactlyElementsOf(GroundedService.ALLOWED_FIELDS);
        assertThat(result.sourceContext().recordCount()).isEqualTo(1);
    }

    @Test
    void returnsEmptyContextWhenNoRecordsAreGrounded() {
        when(carePlatformClient.fetchServices(isNull(), isNull()))
                .thenReturn(List.of(service("Farmácia Não Verificada", "PHARMACY", false)));

        VerifiedServiceGrounding.GroundingResult result = grounding.retrieve(null);

        assertThat(result.records()).isEmpty();
        assertThat(result.sourceContext().recordCount()).isZero();
    }

    @Test
    void wrapsClientFailuresAsControlledUnavailability() {
        when(carePlatformClient.fetchServices(isNull(), isNull()))
                .thenThrow(new ResourceAccessException("connection refused"));

        assertThatThrownBy(() -> grounding.retrieve(null))
                .isInstanceOf(AssistantUnavailableException.class);
    }

    private static CareServiceDto service(String name, String type, boolean verified) {
        return new CareServiceDto(
                UUID.randomUUID(),
                UUID.randomUUID(),
                name,
                type,
                "Boavista",
                "09:00 - 18:00",
                "OPEN",
                "+351220000001",
                verified,
                Instant.parse("2026-10-12T15:30:00Z")
        );
    }
}
