package pt.isep.careplatform.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pt.isep.careplatform.dto.OrganisationAdminResponse;
import pt.isep.careplatform.dto.OrganisationDetailsRequest;
import pt.isep.careplatform.entity.Organisation;
import pt.isep.careplatform.repository.OrganisationRepository;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrganisationVerificationService {

    private final OrganisationRepository repository;

    public OrganisationVerificationService(OrganisationRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<OrganisationAdminResponse> getAll() {
        return repository.findAll().stream()
                .map(OrganisationAdminResponse::from)
                .toList();
    }

    public OrganisationAdminResponse setVerification(UUID organisationId, boolean verified) {
        Organisation organisation = findOrganisation(organisationId);
        organisation.setVerified(verified);
        return OrganisationAdminResponse.from(organisation);
    }

    public OrganisationAdminResponse updateDetails(UUID organisationId, OrganisationDetailsRequest request) {
        Organisation organisation = findOrganisation(organisationId);
        organisation.updatePublicDetails(
                optionalValue(request.officialEmail(), organisation.getOfficialEmail()),
                optionalValue(request.officialPhone(), organisation.getOfficialPhone()),
                optionalWebsite(request.website(), organisation.getWebsite()),
                optionalValue(request.coarseLocation(), organisation.getCoarseLocation())
        );
        return OrganisationAdminResponse.from(organisation);
    }

    private Organisation findOrganisation(UUID organisationId) {
        return repository.findById(organisationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organização não encontrada"));
    }

    private String optionalValue(String value, String currentValue) {
        if (value == null) {
            return currentValue;
        }
        String trimmedValue = value.trim();
        return trimmedValue.isEmpty() ? null : trimmedValue;
    }

    private String optionalWebsite(String value, String currentValue) {
        if (value == null) {
            return currentValue;
        }

        String website = value.trim();
        if (website.isEmpty()) {
            return null;
        }

        try {
            URI uri = URI.create(website);
            String scheme = uri.getScheme();
            if (uri.getHost() == null
                    || uri.getUserInfo() != null
                    || !("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O website deve ser um endereço HTTP ou HTTPS válido");
        }
        return website;
    }
}
