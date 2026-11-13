package pt.isep.careplatform.dto;

import pt.isep.careplatform.entity.User;
import pt.isep.careplatform.entity.UserRole;

import java.util.UUID;

public record UserResponse(UUID id, String email, UserRole role) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getRole());
    }
}
