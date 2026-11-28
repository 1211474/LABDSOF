package pt.isep.careplatform.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pt.isep.careplatform.entity.User;
import pt.isep.careplatform.entity.UserRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = "organisation")
    List<User> findByRoleOrderByEmailAsc(UserRole role);
}
