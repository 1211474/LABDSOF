package pt.isep.careplatform.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pt.isep.careplatform.dto.CareRecipientRequest;
import pt.isep.careplatform.dto.CareRecipientResponse;
import pt.isep.careplatform.dto.CaregiverProfileRequest;
import pt.isep.careplatform.dto.CaregiverProfileResponse;
import pt.isep.careplatform.service.CareProfileService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/caregivers")
@PreAuthorize("hasRole('CAREGIVER')")
public class CareProfileController {

    private final CareProfileService service;

    public CareProfileController(CareProfileService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CaregiverProfileResponse create(
            Authentication authentication,
            @Valid @RequestBody CaregiverProfileRequest request
    ) {
        return service.create(authentication.getName(), request);
    }

    @GetMapping("/me")
    public CaregiverProfileResponse get(Authentication authentication) {
        return service.get(authentication.getName());
    }

    @PatchMapping("/{caregiverId}")
    public CaregiverProfileResponse update(
            Authentication authentication,
            @PathVariable UUID caregiverId,
            @Valid @RequestBody CaregiverProfileRequest request
    ) {
        return service.update(authentication.getName(), caregiverId, request);
    }

    @PostMapping("/{caregiverId}/recipients")
    @ResponseStatus(HttpStatus.CREATED)
    public CareRecipientResponse addRecipient(
            Authentication authentication,
            @PathVariable UUID caregiverId,
            @Valid @RequestBody CareRecipientRequest request
    ) {
        return service.addRecipient(authentication.getName(), caregiverId, request);
    }

    @PatchMapping("/{caregiverId}/recipients/{recipientId}")
    public CareRecipientResponse updateRecipient(
            Authentication authentication,
            @PathVariable UUID caregiverId,
            @PathVariable UUID recipientId,
            @Valid @RequestBody CareRecipientRequest request
    ) {
        return service.updateRecipient(authentication.getName(), caregiverId, recipientId, request);
    }

    @DeleteMapping("/{caregiverId}/recipients/{recipientId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeRecipient(
            Authentication authentication,
            @PathVariable UUID caregiverId,
            @PathVariable UUID recipientId
    ) {
        service.removeRecipient(authentication.getName(), caregiverId, recipientId);
    }
}
