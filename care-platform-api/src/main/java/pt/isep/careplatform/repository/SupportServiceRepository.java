package pt.isep.careplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.isep.careplatform.entity.ServiceType;
import pt.isep.careplatform.entity.SupportService;

import java.util.List;
import java.util.UUID;

public interface SupportServiceRepository extends JpaRepository<SupportService, UUID> {

    List<SupportService> findByTypeOrderByNameAsc(ServiceType type);

    List<SupportService> findByCoarseLocationIgnoreCaseOrderByNameAsc(String coarseLocation);

    List<SupportService> findByTypeAndCoarseLocationIgnoreCaseOrderByNameAsc(ServiceType type, String coarseLocation);
}
