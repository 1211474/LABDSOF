package pt.isep.careplatform.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.isep.careplatform.dto.ServiceResponse;
import pt.isep.careplatform.entity.ServiceType;
import pt.isep.careplatform.service.ServiceCatalogService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/services")
public class ServiceController {

    private final ServiceCatalogService serviceCatalogService;

    public ServiceController(ServiceCatalogService serviceCatalogService) {
        this.serviceCatalogService = serviceCatalogService;
    }

    @GetMapping
    public List<ServiceResponse> search(
            @RequestParam(required = false) ServiceType type,
            @RequestParam(required = false) String coarseLocation
    ) {
        return serviceCatalogService.search(type, coarseLocation);
    }
}
