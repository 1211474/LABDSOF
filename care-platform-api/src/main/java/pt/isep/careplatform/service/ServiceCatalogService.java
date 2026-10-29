package pt.isep.careplatform.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.isep.careplatform.dto.ServiceResponse;
import pt.isep.careplatform.entity.ServiceType;
import pt.isep.careplatform.entity.SupportService;
import pt.isep.careplatform.repository.SupportServiceRepository;

import java.util.List;

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
}
