package pt.isep.careplatform.dto;

import jakarta.validation.constraints.NotNull;

public record OrganisationVerificationRequest(@NotNull Boolean verified) {
}
