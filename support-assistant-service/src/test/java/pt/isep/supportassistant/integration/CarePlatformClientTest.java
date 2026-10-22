package pt.isep.supportassistant.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import pt.isep.supportassistant.dto.CareServiceDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class CarePlatformClientTest {

    @Test
    void fetchesServicesWithFilters() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://localhost:8080");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        CarePlatformClient client = new CarePlatformClient(builder.build());

        server.expect(requestTo("http://localhost:8080/api/v1/services?type=PHARMACY&coarseLocation=Boavista"))
                .andRespond(withSuccess("""
                        [
                          {
                            "id": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
                            "organisationId": "11111111-1111-1111-1111-111111111111",
                            "name": "Farmácia da Boavista",
                            "type": "PHARMACY",
                            "coarseLocation": "Boavista",
                            "operatingHours": "08:00 - 22:00",
                            "currentStatus": "OPEN",
                            "contactPhone": "+351220000001",
                            "verified": true,
                            "lastUpdated": "2026-10-12T15:30:00Z"
                          }
                        ]
                        """, MediaType.APPLICATION_JSON));

        List<CareServiceDto> services = client.fetchServices("PHARMACY", "Boavista");

        assertThat(services).hasSize(1);
        assertThat(services.getFirst().name()).isEqualTo("Farmácia da Boavista");
        server.verify();
    }

    @Test
    void fetchesAllServicesWhenNoFiltersAreGiven() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://localhost:8080");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        CarePlatformClient client = new CarePlatformClient(builder.build());

        server.expect(requestTo("http://localhost:8080/api/v1/services"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        List<CareServiceDto> services = client.fetchServices(null, null);

        assertThat(services).isEmpty();
        server.verify();
    }
}
