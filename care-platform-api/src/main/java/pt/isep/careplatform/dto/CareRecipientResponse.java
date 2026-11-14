package pt.isep.careplatform.dto;

import pt.isep.careplatform.entity.CareRecipient;

import java.util.Set;
import java.util.UUID;

public record CareRecipientResponse(UUID id, String displayName, Set<String> coarseAccessibilityTags) {

    public static CareRecipientResponse from(CareRecipient recipient) {
        return new CareRecipientResponse(recipient.getId(), recipient.getDisplayName(),
                recipient.getCoarseAccessibilityTags().stream()
                        .map(tag -> tag.name().toLowerCase())
                        .collect(java.util.stream.Collectors.toUnmodifiableSet())
        );
    }
}
