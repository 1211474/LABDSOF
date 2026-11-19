package pt.isep.careplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.isep.careplatform.entity.CareRecipient;

import java.util.List;
import java.util.UUID;

public interface CareRecipientRepository extends JpaRepository<CareRecipient, UUID> {

    List<CareRecipient> findByCaregiver_IdOrderById(UUID caregiverId);
}
