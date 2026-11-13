package pt.isep.careplatform.dto;

import pt.isep.careplatform.entity.UserRole;

public record LoginResponse(String token, UserRole role) {
}
