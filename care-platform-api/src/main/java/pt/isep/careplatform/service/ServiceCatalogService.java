package pt.isep.careplatform.service;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pt.isep.careplatform.dto.ServiceResponse;
import pt.isep.careplatform.entity.ServiceType;
import pt.isep.careplatform.entity.SupportService;
import pt.isep.careplatform.repository.SupportServiceRepository;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ServiceCatalogService {

    private final SupportServiceRepository repository;

    public ServiceCatalogService(SupportServiceRepository repository) {
        this.repository = repository;
    }

    public List<ServiceResponse> search(ServiceType type, String coarseLocation) {
        boolean hasLocation = coarseLocation != null && !coarseLocation.isBlank();

        List<SupportService> services;
        if (type != null && hasLocation) {
            services = repository.findByTypeAndCoarseLocationIgnoreCaseOrderByNameAsc(type, coarseLocation.trim());
        } else if (type != null) {
            services = repository.findByTypeOrderByNameAsc(type);
        } else if (hasLocation) {
            services = repository.findByCoarseLocationIgnoreCaseOrderByNameAsc(coarseLocation.trim());
        } else {
            services = repository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        }

        return services.stream()
                .map(ServiceResponse::from)
                .toList();
    }

    public ServiceResponse getById(UUID id) {
        SupportService service = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado"));
        return ServiceResponse.from(service);
    }
}
