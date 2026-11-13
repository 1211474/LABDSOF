package pt.isep.supportassistant.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.ResourceAccessException;
import pt.isep.supportassistant.dto.AiNavigateResponse;
import pt.isep.supportassistant.dto.CareServiceDto;
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
class AiNavigatorServiceTest {

    @Mock
    private CarePlatformClient carePlatformClient;

    private AiNavigatorService aiNavigatorService;

    @BeforeEach
    void setUp() {
        aiNavigatorService = new AiNavigatorService(carePlatformClient, new AssistantQueryParser());
    }

    @Test
    void filtersOpenPharmaciesInBoavistaAndBuildsReadableAnswer() {
        CareServiceDto openPharmacy = service("Farmácia da Boavista", "PHARMACY", "Boavista", "OPEN");
        CareServiceDto closedPharmacy = service("Farmácia Fechada", "PHARMACY", "Boavista", "TEMPORARILY_CLOSED");
        CareServiceDto otherLocation = service("Farmácia de Campanhã", "PHARMACY", "Campanhã", "OPEN");

        when(carePlatformClient.fetchServices(eq("PHARMACY"), isNull()))
                .thenReturn(List.of(closedPharmacy, otherLocation, openPharmacy));

        AiNavigateResponse response = aiNavigatorService.navigate("farmácias abertas perto da Boavista");

        verify(carePlatformClient).fetchServices("PHARMACY", null);
        assertThat(response.services()).containsExactly(openPharmacy);
        assertThat(response.answer())
                .contains("Encontrei 1 resultado")
                .contains("farmácias")
                .contains("com estado aberto")
                .contains("perto de Boavista")
                .contains("Farmácia da Boavista")
                .contains("estado: aberto")
                .contains("+351220000001");
        assertThat(response.isFallback()).isTrue();
        assertThat(response.limitations()).isNotEmpty();
    }

    @Test
    void understandsEnglishExampleQuestion() {
        CareServiceDto openPharmacy = service("Boavista Pharmacy", "PHARMACY", "Boavista", "OPEN");

        when(carePlatformClient.fetchServices(eq("PHARMACY"), isNull()))
                .thenReturn(List.of(openPharmacy));

        AiNavigateResponse response = aiNavigatorService.navigate("pharmacies open near Boavista");

        assertThat(response.services()).containsExactly(openPharmacy);
        assertThat(response.answer()).contains("perto de Boavista", "com estado aberto");
    }

    @Test
    void returnsClosedServicesWhenAskedForClosedOnes() {
        CareServiceDto openPharmacy = service("Farmácia Aberta", "PHARMACY", "Boavista", "OPEN");
        CareServiceDto closedPharmacy = service("Farmácia Central", "PHARMACY", "Boavista", "CLOSED");
        CareServiceDto temporarilyClosed = service("Farmácia da Esquina", "PHARMACY", "Boavista", "TEMPORARILY_CLOSED");

        when(carePlatformClient.fetchServices(eq("PHARMACY"), isNull()))
                .thenReturn(List.of(openPharmacy, closedPharmacy, temporarilyClosed));

        AiNavigateResponse response = aiNavigatorService.navigate("farmácias fechadas");

        assertThat(response.services()).containsExactly(temporarilyClosed, closedPharmacy);
        assertThat(response.answer())
                .contains("com estado fechado")
                .contains("temporariamente fechado")
                .contains("fechado");
    }

    @Test
    void explainsWhenNoServiceMatches() {
        CareServiceDto openPharmacy = service("Farmácia da Boavista", "PHARMACY", "Boavista", "OPEN");

        when(carePlatformClient.fetchServices(eq("PHARMACY"), isNull()))
                .thenReturn(List.of(openPharmacy));

        AiNavigateResponse response = aiNavigatorService.navigate("farmácias fechadas perto da Boavista");

        assertThat(response.services()).isEmpty();
        assertThat(response.answer())
                .contains("Não encontrei farmácias")
                .contains("com estado fechado")
                .contains("perto de Boavista")
                .contains("Tente outra zona");
    }

    @Test
    void explainsWhenRequestedLocationHasNoServices() {
        CareServiceDto openPharmacy = service("Farmácia da Boavista", "PHARMACY", "Boavista", "OPEN");

        when(carePlatformClient.fetchServices(eq("PHARMACY"), isNull()))
                .thenReturn(List.of(openPharmacy));

        AiNavigateResponse response = aiNavigatorService.navigate("farmácias perto de Matosinhos");

        assertThat(response.services()).isEmpty();
        assertThat(response.answer()).contains("Não encontrei farmácias perto de Matosinhos");
    }

    @Test
    void pointsToFullListWhenThereAreManyResults() {
        List<CareServiceDto> services = List.of(
                service("Farmácia A", "PHARMACY", "Boavista", "OPEN"),
                service("Farmácia B", "PHARMACY", "Campanhã", "OPEN"),
                service("Farmácia C", "PHARMACY", "Cedofeita", "OPEN"),
                service("Farmácia D", "PHARMACY", "Bonfim", "OPEN"),
                service("Farmácia E", "PHARMACY", "Ramalde", "OPEN")
        );

        when(carePlatformClient.fetchServices(eq("PHARMACY"), isNull()))
                .thenReturn(services);

        AiNavigateResponse response = aiNavigatorService.navigate("farmácias");

        assertThat(response.services()).hasSize(5);
        assertThat(response.answer())
                .contains("Encontrei 5 resultados")
                .contains("A lista completa está apresentada abaixo");
    }

    @Test
    void ranksOpenServicesFirstWhenNoStatusIsRequested() {
        CareServiceDto unknown = service("Farmácia Sem Estado", "PHARMACY", "Boavista", "UNKNOWN");
        CareServiceDto closed = service("Farmácia Fechada", "PHARMACY", "Boavista", "CLOSED");
        CareServiceDto open = service("Farmácia Aberta", "PHARMACY", "Boavista", "OPEN");
        CareServiceDto temporarilyClosed = service("Farmácia Temporária", "PHARMACY", "Boavista", "TEMPORARILY_CLOSED");

        when(carePlatformClient.fetchServices(eq("PHARMACY"), isNull()))
                .thenReturn(List.of(unknown, closed, open, temporarilyClosed));

        AiNavigateResponse response = aiNavigatorService.navigate("farmácias na Boavista");

        assertThat(response.services()).containsExactly(open, temporarilyClosed, closed, unknown);
    }

    @Test
    void labelsUnknownStatusInAnswer() {
        CareServiceDto unknown = service("Farmácia Sem Estado", "PHARMACY", "Boavista", "UNKNOWN");

        when(carePlatformClient.fetchServices(eq("PHARMACY"), isNull()))
                .thenReturn(List.of(unknown));

        AiNavigateResponse response = aiNavigatorService.navigate("farmácias na Boavista");

        assertThat(response.answer()).contains("estado: desconhecido");
    }

    @Test
    void returnsAllServicesWhenNoIntentIsRecognised() {
        CareServiceDto pharmacy = service("A Farmácia", "PHARMACY", "Boavista", "OPEN");
        CareServiceDto dayCentre = service("B Centro de Dia", "DAY_CENTRE", "Campanhã", "OPEN");

        when(carePlatformClient.fetchServices(isNull(), isNull()))
                .thenReturn(List.of(dayCentre, pharmacy));

        AiNavigateResponse response = aiNavigatorService.navigate("preciso de ajuda");

        assertThat(response.services()).containsExactly(pharmacy, dayCentre);
        assertThat(response.answer())
                .contains("Encontrei 2 resultados")
                .doesNotContain("para ")
                .doesNotContain("perto de");
    }

    @Test
    void usesDirectorySpellingForAccentInsensitiveLocation() {
        CareServiceDto dayCentre = service("Centro de Dia de Campanhã", "DAY_CENTRE", "Campanhã", "OPEN");

        when(carePlatformClient.fetchServices(eq("DAY_CENTRE"), isNull()))
                .thenReturn(List.of(dayCentre));

        AiNavigateResponse response = aiNavigatorService.navigate("centros de dia em campanha");

        assertThat(response.services()).containsExactly(dayCentre);
        assertThat(response.answer()).contains("perto de Campanhã");
    }

    @Test
    void acceptsLocationDetectedFromProximityPhrase() {
        CareServiceDto pharmacy = service("Farmácia de Fânzeres", "PHARMACY", "Fânzeres", "OPEN");

        when(carePlatformClient.fetchServices(eq("PHARMACY"), isNull()))
                .thenReturn(List.of(pharmacy));

        AiNavigateResponse response = aiNavigatorService.navigate("farmácias near Fânzeres");

        assertThat(response.services()).containsExactly(pharmacy);
        assertThat(response.answer()).contains("perto de Fânzeres");
    }

    @Test
    void excludesServicesFromUnverifiedOrganisations() {
        CareServiceDto verifiedPharmacy = service("Farmácia da Boavista", "PHARMACY", "Boavista", "OPEN");
        CareServiceDto unverifiedPharmacy = service(
                "Farmácia Não Verificada", "PHARMACY", "Boavista", "OPEN", false);

        when(carePlatformClient.fetchServices(eq("PHARMACY"), isNull()))
                .thenReturn(List.of(verifiedPharmacy, unverifiedPharmacy));

        AiNavigateResponse response = aiNavigatorService.navigate("farmácias na Boavista");

        assertThat(response.services()).containsExactly(verifiedPharmacy);
        assertThat(response.answer()).contains("Encontrei 1 resultado");
    }

    @Test
    void doesNotPresentUnverifiedServicesAsResults() {
        CareServiceDto unverifiedService = service(
                "Apoio ao Domicílio dos Vizinhos", "HOME_SUPPORT", "Boavista", "OPEN", false);

        when(carePlatformClient.fetchServices(eq("HOME_SUPPORT"), isNull()))
                .thenReturn(List.of(unverifiedService));

        AiNavigateResponse response = aiNavigatorService.navigate("apoio ao domicílio perto da Boavista");

        assertThat(response.services()).isEmpty();
        assertThat(response.answer())
                .contains("Não encontrei")
                .contains("apoio ao domicílio");
    }

    @Test
    void throwsControlledExceptionWhenCarePlatformIsUnavailable() {
        when(carePlatformClient.fetchServices(isNull(), isNull()))
                .thenThrow(new ResourceAccessException("connection refused"));

        assertThatThrownBy(() -> aiNavigatorService.navigate("ajuda"))
                .isInstanceOf(AssistantUnavailableException.class);
    }

    private static CareServiceDto service(String name, String type, String location, String status) {
        return service(name, type, location, status, true);
    }

    private static CareServiceDto service(
            String name, String type, String location, String status, boolean verified) {
        return new CareServiceDto(
                UUID.randomUUID(),
                UUID.randomUUID(),
                name,
                type,
                location,
                "09:00 - 18:00",
                status,
                "+351220000001",
                verified,
                Instant.parse("2026-10-12T15:30:00Z")
        );
    }
}
