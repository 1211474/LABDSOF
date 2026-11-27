package pt.isep.careplatform.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pt.isep.careplatform.dto.ProviderAdminResponse;
import pt.isep.careplatform.entity.User;
import pt.isep.careplatform.entity.UserRole;
import pt.isep.careplatform.repository.OrganisationRepository;
import pt.isep.careplatform.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProviderAdministrationService {

    private final UserRepository userRepository;
    private final OrganisationRepository organisationRepository;

    public ProviderAdministrationService(
            UserRepository userRepository,
            OrganisationRepository organisationRepository
    ) {
        this.userRepository = userRepository;
        this.organisationRepository = organisationRepository;
    }

    @Transactional(readOnly = true)
    public List<ProviderAdminResponse> getProviders() {
        return userRepository.findByRoleOrderByEmailAsc(UserRole.PROVIDER).stream()
                .map(ProviderAdminResponse::from)
                .toList();
    }

    public ProviderAdminResponse assignOrganisation(UUID providerId, UUID organisationId) {
        User provider = userRepository.findById(providerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prestador não encontrado"));
        if (provider.getRole() != UserRole.PROVIDER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O utilizador indicado não é um prestador");
        }

        var organisation = organisationRepository.findById(organisationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organização não encontrada"));
        provider.assignToOrganisation(organisation);
        return ProviderAdminResponse.from(provider);
    }
}
