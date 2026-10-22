package pt.isep.careplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.isep.careplatform.entity.Organisation;

import java.util.UUID;

public interface OrganisationRepository extends JpaRepository<Organisation, UUID> {
}
