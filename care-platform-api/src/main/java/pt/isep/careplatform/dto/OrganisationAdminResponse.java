package pt.isep.careplatform.dto;

import pt.isep.careplatform.entity.Organisation;

import java.time.Instant;
import java.util.UUID;

public record OrganisationAdminResponse(
        UUID id,
        String name,
        boolean verified,
        Instant verifiedAt,
        String officialEmail,
        String officialPhone,
        String website,
        String coarseLocation
) {

    public static OrganisationAdminResponse from(Organisation organisation) {
        return new OrganisationAdminResponse(
                organisation.getId(),
                organisation.getName(),
                organisation.isVerified(),
                organisation.getVerifiedAt(),
                organisation.getOfficialEmail(),
                organisation.getOfficialPhone(),
                organisation.getWebsite(),
                organisation.getCoarseLocation()
        );
    }
}
