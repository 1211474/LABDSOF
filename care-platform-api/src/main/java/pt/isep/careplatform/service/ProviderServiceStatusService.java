package pt.isep.careplatform.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pt.isep.careplatform.dto.ServiceResponse;
import pt.isep.careplatform.dto.ServiceStatusUpdateRequest;
import pt.isep.careplatform.entity.SupportService;
import pt.isep.careplatform.entity.User;
import pt.isep.careplatform.entity.UserRole;
import pt.isep.careplatform.repository.SupportServiceRepository;
import pt.isep.careplatform.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProviderServiceStatusService {

    private final UserRepository userRepository;
    private final SupportServiceRepository supportServiceRepository;

    public ProviderServiceStatusService(
            UserRepository userRepository,
            SupportServiceRepository supportServiceRepository
    ) {
        this.userRepository = userRepository;
        this.supportServiceRepository = supportServiceRepository;
    }

    @Transactional(readOnly = true)
    public List<ServiceResponse> getMyServices(String email) {
        User provider = getProvider(email);
        if (provider.getOrganisation() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Prestador sem organização associada");
        }

        return supportServiceRepository.findByOrganisationIdOrderByNameAsc(provider.getOrganisation().getId())
                .stream()
                .map(ServiceResponse::from)
                .toList();
    }

    public ServiceResponse updateStatus(UUID serviceId, String email, ServiceStatusUpdateRequest request) {
        User provider = getProvider(email);
        if (provider.getOrganisation() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Prestador sem organização associada");
        }
        if (!provider.getOrganisation().isVerified()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "A organização do prestador não está verificada");
        }

        SupportService supportService = supportServiceRepository.findById(serviceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado"));
        if (!supportService.getOrganisation().getId().equals(provider.getOrganisation().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Não pode atualizar este serviço");
        }

        String operatingHours = request.operatingHours() == null
                ? supportService.getOperatingHours()
                : request.operatingHours().trim();
        String officialNotice = request.officialNotice() == null
                ? supportService.getOfficialNotice()
                : request.officialNotice().trim();
        supportService.updateStatus(request.status(), operatingHours, officialNotice);
        return ServiceResponse.from(supportService);
    }

    private User getProvider(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if (user.getRole() != UserRole.PROVIDER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso reservado a prestadores");
        }
        return user;
    }
}
