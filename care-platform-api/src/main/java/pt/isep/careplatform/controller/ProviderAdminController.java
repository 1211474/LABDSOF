package pt.isep.careplatform.controller;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.isep.careplatform.dto.ProviderAdminResponse;
import pt.isep.careplatform.dto.ProviderOrganisationAssignmentRequest;
import pt.isep.careplatform.service.ProviderAdministrationService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/providers")
@PreAuthorize("hasRole('PLATFORM_ADMIN')")
public class ProviderAdminController {

    private final ProviderAdministrationService service;

    public ProviderAdminController(ProviderAdministrationService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProviderAdminResponse> getProviders() {
        return service.getProviders();
    }

    @PutMapping("/{providerId}/organisation")
    public ProviderAdminResponse assignOrganisation(
            @PathVariable UUID providerId,
            @Valid @RequestBody ProviderOrganisationAssignmentRequest request
    ) {
        return service.assignOrganisation(providerId, request.organisationId());
    }
}
