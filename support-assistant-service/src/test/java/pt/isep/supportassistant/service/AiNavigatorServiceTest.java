package pt.isep.supportassistant.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.ResourceAccessException;
import pt.isep.supportassistant.dto.AiNavigateResponse;
import pt.isep.supportassistant.dto.CareServiceDto;
import pt.isep.supportassistant.integration.CarePlatformClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiNavigatorServiceTest {

    @Mock
    private CarePlatformClient carePlatformClient;

    @InjectMocks
    private AiNavigatorService aiNavigatorService;

    @Test
    void extractsTypeAndLocationAndReturnsVerifiedServices() {
        CareServiceDto service = new CareServiceDto(
                null,
                null,
                "Farmácia da Boavista",
                "PHARMACY",
                "Boavista",
                "08:00 - 22:00",
                "OPEN",
                "+351220000001",
                true,
                null
        );
        when(carePlatformClient.fetchServices("PHARMACY", "Boavista")).thenReturn(List.of(service));

        AiNavigateResponse response = aiNavigatorService.navigate("pharmacies near Boavista");

        verify(carePlatformClient).fetchServices("PHARMACY", "Boavista");
        assertThat(response.services()).containsExactly(service);
        assertThat(response.isFallback()).isTrue();
        assertThat(response.answer()).contains("1 serviço");
        assertThat(response.limitations()).isNotEmpty();
    }

    @Test
    void throwsControlledExceptionWhenCarePlatformIsUnavailable() {
        when(carePlatformClient.fetchServices(null, null))
                .thenThrow(new ResourceAccessException("connection refused"));

        assertThatThrownBy(() -> aiNavigatorService.navigate("ajuda"))
                .isInstanceOf(AssistantUnavailableException.class);
    }
}
