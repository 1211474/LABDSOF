package pt.isep.careplatform.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProviderOrganisationAssignmentRequest(@NotNull UUID organisationId) {
}
