package pt.isep.careplatform.dto;

import pt.isep.careplatform.entity.User;

import java.util.UUID;

public record ProviderAdminResponse(
        UUID id,
        String email,
        UUID organisationId,
        String organisationName,
        boolean organisationVerified
) {

    public static ProviderAdminResponse from(User provider) {
        var organisation = provider.getOrganisation();
        return new ProviderAdminResponse(
                provider.getId(),
                provider.getEmail(),
                organisation == null ? null : organisation.getId(),
                organisation == null ? null : organisation.getName(),
                organisation != null && organisation.isVerified()
        );
    }
}
