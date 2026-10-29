package pt.isep.supportassistant.integration;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import pt.isep.supportassistant.dto.CareServiceDto;

import java.util.List;

@Component
public class CarePlatformClient {

    private final RestClient carePlatformRestClient;

    public CarePlatformClient(RestClient carePlatformRestClient) {
        this.carePlatformRestClient = carePlatformRestClient;
    }

    public List<CareServiceDto> fetchServices(String type, String coarseLocation) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/api/v1/services");

        if (type != null) {
            uriBuilder.queryParam("type", type);
        }
        if (coarseLocation != null) {
            uriBuilder.queryParam("coarseLocation", coarseLocation);
        }

        String path = uriBuilder.build().encode().toUriString();

        CareServiceDto[] response = carePlatformRestClient.get()
                .uri(path)
                .retrieve()
                .body(CareServiceDto[].class);

        return response == null ? List.of() : List.of(response);
    }
}
