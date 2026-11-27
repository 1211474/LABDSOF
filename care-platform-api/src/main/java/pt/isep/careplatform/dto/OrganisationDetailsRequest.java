package pt.isep.careplatform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record OrganisationDetailsRequest(
        @Email @Size(max = 320) String officialEmail,
        @Pattern(regexp = "[+0-9() .-]*") @Size(max = 40) String officialPhone,
        @Size(max = 2048) String website,
        @Size(max = 120) String coarseLocation
) {
}
