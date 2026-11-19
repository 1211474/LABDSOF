package pt.isep.careplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CaregiverProfileRequest(
        @NotBlank
        @Size(max = 120)
        String displayName,
        @NotBlank
        @Size(max = 40)
        @Pattern(regexp = "^[+0-9][0-9 ()-]{6,38}$")
        String phoneNumber,
        @NotBlank
        @Size(max = 120)
        String coarseLocation
) {
}
