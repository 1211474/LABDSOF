package pt.isep.careplatform.dto;

import pt.isep.careplatform.entity.User;
import pt.isep.careplatform.entity.UserRole;
import pt.isep.careplatform.security.RolePermissions;

import java.util.UUID;
import java.util.Set;

public record UserResponse(UUID id, String email, UserRole role, Set<String> permissions) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getRole(), RolePermissions.forRole(user.getRole()));
    }
}
