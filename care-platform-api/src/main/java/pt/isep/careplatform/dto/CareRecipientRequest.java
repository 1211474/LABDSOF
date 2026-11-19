package pt.isep.careplatform.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CareRecipientRequest(
        @NotBlank
        @Size(max = 120)
        String displayName,
        @NotNull
        @Size(max = 10)
        Set<String> coarseAccessibilityTags
) {
}
