package pt.isep.careplatform.security;

import pt.isep.careplatform.entity.UserRole;

import java.util.Set;

public final class RolePermissions {

    public static final String SEARCH_SERVICES = "SEARCH_SERVICES";
    public static final String ASK_NAVIGATOR = "ASK_NAVIGATOR";
    public static final String MANAGE_SUPPORT_REQUESTS = "MANAGE_SUPPORT_REQUESTS";
    public static final String MANAGE_SERVICE_STATUS = "MANAGE_SERVICE_STATUS";
    public static final String VIEW_SERVICE_DIRECTORY = "VIEW_SERVICE_DIRECTORY";
    public static final String VERIFY_ORGANISATIONS = "VERIFY_ORGANISATIONS";

    private RolePermissions() {
    }

    public static Set<String> forRole(UserRole role) {
        return switch (role) {
            case CAREGIVER -> Set.of(SEARCH_SERVICES, ASK_NAVIGATOR, MANAGE_SUPPORT_REQUESTS);
            case PROVIDER -> Set.of(MANAGE_SERVICE_STATUS, VIEW_SERVICE_DIRECTORY);
            case PLATFORM_ADMIN -> Set.of(VERIFY_ORGANISATIONS);
        };
    }
}
