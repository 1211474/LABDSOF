package pt.isep.careplatform.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pt.isep.careplatform.dto.CareRecipientRequest;
import pt.isep.careplatform.dto.CareRecipientResponse;
import pt.isep.careplatform.dto.CaregiverProfileRequest;
import pt.isep.careplatform.dto.CaregiverProfileResponse;
import pt.isep.careplatform.dto.EmergencyContactRequest;
import pt.isep.careplatform.dto.EmergencyContactResponse;
import pt.isep.careplatform.entity.AccessibilityTag;
import pt.isep.careplatform.entity.CareRecipient;
import pt.isep.careplatform.entity.Caregiver;
import pt.isep.careplatform.entity.EmergencyContact;
import pt.isep.careplatform.entity.User;
import pt.isep.careplatform.entity.UserRole;
import pt.isep.careplatform.repository.CareRecipientRepository;
import pt.isep.careplatform.repository.CaregiverRepository;
import pt.isep.careplatform.repository.EmergencyContactRepository;
import pt.isep.careplatform.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class CareProfileService {

    private final CaregiverRepository caregiverRepository;
    private final CareRecipientRepository recipientRepository;
    private final EmergencyContactRepository emergencyContactRepository;
    private final UserRepository userRepository;

    public CareProfileService(
            CaregiverRepository caregiverRepository,
            CareRecipientRepository recipientRepository,
            EmergencyContactRepository emergencyContactRepository,
            UserRepository userRepository
    ) {
        this.caregiverRepository = caregiverRepository;
        this.recipientRepository = recipientRepository;
        this.emergencyContactRepository = emergencyContactRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CaregiverProfileResponse create(String email, CaregiverProfileRequest request) {
        User user = caregiverUser(email);
        if (caregiverRepository.findByUser_EmailIgnoreCase(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O perfil do cuidador já existe");
        }

        Caregiver caregiver = caregiverRepository.save(new Caregiver(
                user,
                request.displayName().trim(),
                request.phoneNumber().trim(),
                request.coarseLocation().trim()
        ));
        return profileResponse(caregiver);
    }

    @Transactional
    public CaregiverProfileResponse update(String email, UUID caregiverId, CaregiverProfileRequest request) {
        Caregiver caregiver = ownedCaregiver(email, caregiverId);
        caregiver.update(
                request.displayName().trim(),
                request.phoneNumber().trim(),
                request.coarseLocation().trim()
        );
        return profileResponse(caregiver);
    }

    @Transactional(readOnly = true)
    public CaregiverProfileResponse get(String email) {
        return caregiverRepository.findByUser_EmailIgnoreCase(email)
                .map(this::profileResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil do cuidador não encontrado"));
    }

    @Transactional
    public CareRecipientResponse addRecipient(String email, UUID caregiverId, CareRecipientRequest request) {
        Caregiver caregiver = ownedCaregiver(email, caregiverId);
        CareRecipient recipient = recipientRepository.save(
                new CareRecipient(caregiver, request.displayName().trim(), parseTags(request.coarseAccessibilityTags()))
        );
        return CareRecipientResponse.from(recipient);
    }

    @Transactional
    public CareRecipientResponse updateRecipient(
            String email,
            UUID caregiverId,
            UUID recipientId,
            CareRecipientRequest request
    ) {
        Caregiver caregiver = ownedCaregiver(email, caregiverId);
        CareRecipient recipient = ownedRecipient(caregiver, recipientId);
        recipient.update(request.displayName().trim(), parseTags(request.coarseAccessibilityTags()));
        return CareRecipientResponse.from(recipient);
    }

    @Transactional
    public void removeRecipient(String email, UUID caregiverId, UUID recipientId) {
        Caregiver caregiver = ownedCaregiver(email, caregiverId);
        recipientRepository.delete(ownedRecipient(caregiver, recipientId));
    }

    @Transactional
    public EmergencyContactResponse addEmergencyContact(String email, UUID caregiverId, EmergencyContactRequest request) {
        Caregiver caregiver = ownedCaregiver(email, caregiverId);
        EmergencyContact contact = emergencyContactRepository.save(new EmergencyContact(
                caregiver,
                request.displayName().trim(),
                request.phoneNumber().trim(),
                trimOrNull(request.relationship())
        ));
        return EmergencyContactResponse.from(contact);
    }

    @Transactional
    public EmergencyContactResponse updateEmergencyContact(
            String email,
            UUID caregiverId,
            UUID contactId,
            EmergencyContactRequest request
    ) {
        Caregiver caregiver = ownedCaregiver(email, caregiverId);
        EmergencyContact contact = ownedEmergencyContact(caregiver, contactId);
        contact.update(request.displayName().trim(), request.phoneNumber().trim(), trimOrNull(request.relationship()));
        return EmergencyContactResponse.from(contact);
    }

    @Transactional
    public void removeEmergencyContact(String email, UUID caregiverId, UUID contactId) {
        Caregiver caregiver = ownedCaregiver(email, caregiverId);
        emergencyContactRepository.delete(ownedEmergencyContact(caregiver, contactId));
    }

    private Caregiver ownedCaregiver(String email, UUID caregiverId) {
        Caregiver caregiver = caregiverRepository.findById(caregiverId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil do cuidador não encontrado"));
        if (!caregiver.getUser().getEmail().equalsIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Não pode aceder a este perfil");
        }
        return caregiver;
    }

    private User caregiverUser(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if (user.getRole() != UserRole.CAREGIVER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas cuidadores podem criar este perfil");
        }
        return user;
    }

    private CareRecipient ownedRecipient(Caregiver caregiver, UUID recipientId) {
        CareRecipient recipient = recipientRepository.findById(recipientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pessoa acompanhada não encontrada"));
        if (!recipient.getCaregiver().getId().equals(caregiver.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Não pode aceder a esta pessoa acompanhada");
        }
        return recipient;
    }

    private EmergencyContact ownedEmergencyContact(Caregiver caregiver, UUID contactId) {
        EmergencyContact contact = emergencyContactRepository.findById(contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contacto de emergência não encontrado"));
        if (!contact.getCaregiver().getId().equals(caregiver.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Não pode aceder a este contacto de emergência");
        }
        return contact;
    }

    private String trimOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private CaregiverProfileResponse profileResponse(Caregiver caregiver) {
        List<CareRecipientResponse> recipients = recipientRepository
                .findByCaregiver_IdOrderById(caregiver.getId()).stream()
                .map(CareRecipientResponse::from)
                .toList();
        List<EmergencyContactResponse> emergencyContacts = emergencyContactRepository
                .findByCaregiver_IdOrderById(caregiver.getId()).stream()
                .map(EmergencyContactResponse::from)
                .toList();
        return CaregiverProfileResponse.from(caregiver, recipients, emergencyContacts);
    }

    private Set<AccessibilityTag> parseTags(Set<String> values) {
        return values.stream()
                .map(this::parseTag)
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
    }

    private AccessibilityTag parseTag(String value) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "As tags de acessibilidade são inválidas");
        }
        String normalizedValue = value.trim().toLowerCase(Locale.ROOT)
                .replace('-', '_')
                .replace(' ', '_');
        if (normalizedValue.equals("requires_wheelchair_access")) {
            normalizedValue = "wheelchair_accessible";
        }
        String normalized = normalizedValue;
        return Arrays.stream(AccessibilityTag.values())
                .filter(tag -> tag.name().toLowerCase(Locale.ROOT).equals(normalized))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "A tag de acessibilidade não é suportada: " + value));
    }
}
