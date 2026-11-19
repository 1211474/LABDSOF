package pt.isep.careplatform.dto;

import pt.isep.careplatform.entity.EmergencyContact;

import java.util.UUID;

public record EmergencyContactResponse(UUID id, String displayName, String phoneNumber, String relationship) {

    public static EmergencyContactResponse from(EmergencyContact contact) {
        return new EmergencyContactResponse(
                contact.getId(), contact.getDisplayName(), contact.getPhoneNumber(), contact.getRelationship());
    }
}
