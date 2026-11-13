package pt.isep.careplatform.dto;

import pt.isep.careplatform.entity.UserRole;
import pt.isep.careplatform.security.RolePermissions;

import java.util.Set;

public record LoginResponse(String token, UserRole role, Set<String> permissions) {

    public LoginResponse(String token, UserRole role) {
        this(token, role, RolePermissions.forRole(role));
    }
}
