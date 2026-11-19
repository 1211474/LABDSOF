package pt.isep.careplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmergencyContactRequest(
        @NotBlank
        @Size(max = 120)
        String displayName,
        @NotBlank
        @Size(max = 40)
        String phoneNumber,
        @Size(max = 60)
        String relationship
) {
}
