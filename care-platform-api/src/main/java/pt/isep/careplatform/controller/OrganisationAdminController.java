package pt.isep.careplatform.controller;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.isep.careplatform.dto.OrganisationAdminResponse;
import pt.isep.careplatform.dto.OrganisationDetailsRequest;
import pt.isep.careplatform.dto.OrganisationVerificationRequest;
import pt.isep.careplatform.service.OrganisationVerificationService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/organisations")
@PreAuthorize("hasRole('PLATFORM_ADMIN')")
public class OrganisationAdminController {

    private final OrganisationVerificationService service;

    public OrganisationAdminController(OrganisationVerificationService service) {
        this.service = service;
    }

    @GetMapping
    public List<OrganisationAdminResponse> getAll() {
        return service.getAll();
    }

    @PutMapping("/{organisationId}/verification")
    public OrganisationAdminResponse setVerification(
            @PathVariable UUID organisationId,
            @Valid @RequestBody OrganisationVerificationRequest request
    ) {
        return service.setVerification(organisationId, request.verified());
    }

    @PatchMapping("/{organisationId}")
    public OrganisationAdminResponse updateDetails(
            @PathVariable UUID organisationId,
            @Valid @RequestBody OrganisationDetailsRequest request
    ) {
        return service.updateDetails(organisationId, request);
    }
}
