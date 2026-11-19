package pt.isep.careplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.isep.careplatform.entity.EmergencyContact;

import java.util.List;
import java.util.UUID;

public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, UUID> {

    List<EmergencyContact> findByCaregiver_IdOrderById(UUID caregiverId);
}
