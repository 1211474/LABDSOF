package pt.isep.careplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.isep.careplatform.entity.Caregiver;

import java.util.Optional;
import java.util.UUID;

public interface CaregiverRepository extends JpaRepository<Caregiver, UUID> {

    Optional<Caregiver> findByUser_EmailIgnoreCase(String email);
}
