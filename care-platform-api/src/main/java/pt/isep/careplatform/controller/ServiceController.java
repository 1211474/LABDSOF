package pt.isep.careplatform.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;
import pt.isep.careplatform.dto.ServiceResponse;
import pt.isep.careplatform.dto.ServiceStatusUpdateRequest;
import pt.isep.careplatform.entity.ServiceType;
import pt.isep.careplatform.service.ServiceCatalogService;
import pt.isep.careplatform.service.ProviderServiceStatusService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/services")
public class ServiceController {

    private final ServiceCatalogService serviceCatalogService;
    private final ProviderServiceStatusService providerServiceStatusService;

    public ServiceController(
            ServiceCatalogService serviceCatalogService,
            ProviderServiceStatusService providerServiceStatusService
    ) {
        this.serviceCatalogService = serviceCatalogService;
        this.providerServiceStatusService = providerServiceStatusService;
    }

    @GetMapping
    public List<ServiceResponse> search(
            @RequestParam(required = false) ServiceType type,
            @RequestParam(required = false) String coarseLocation
    ) {
        return serviceCatalogService.search(type, coarseLocation);
    }

    @GetMapping("/{id}")
    public ServiceResponse getById(@PathVariable UUID id) {
        return serviceCatalogService.getById(id);
    }

    @GetMapping("/provider/me")
    @PreAuthorize("hasRole('PROVIDER')")
    public List<ServiceResponse> getMyProviderServices(Authentication authentication) {
        return providerServiceStatusService.getMyServices(authentication.getName());
    }

    @PatchMapping("/{id}/provider-status")
    @PreAuthorize("hasRole('PROVIDER')")
    public ServiceResponse updateProviderStatus(
            @PathVariable UUID id,
            Authentication authentication,
            @Valid @RequestBody ServiceStatusUpdateRequest request
    ) {
        return providerServiceStatusService.updateStatus(id, authentication.getName(), request);
    }
}
