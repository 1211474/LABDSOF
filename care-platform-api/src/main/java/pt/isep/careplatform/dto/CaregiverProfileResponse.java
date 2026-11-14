package pt.isep.careplatform.dto;

import pt.isep.careplatform.entity.Caregiver;

import java.util.List;
import java.util.UUID;

public record CaregiverProfileResponse(
        UUID id,
        UUID userId,
        String displayName,
        String phoneNumber,
        String coarseLocation,
        List<CareRecipientResponse> recipients
) {
    public static CaregiverProfileResponse from(Caregiver caregiver, List<CareRecipientResponse> recipients) {
        return new CaregiverProfileResponse(caregiver.getId(), caregiver.getUser().getId(),
                caregiver.getDisplayName(), caregiver.getPhoneNumber(), caregiver.getCoarseLocation(), recipients);
    }
}
