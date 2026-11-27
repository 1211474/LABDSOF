package pt.isep.careplatform.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pt.isep.careplatform.entity.User;
import pt.isep.careplatform.entity.UserRole;
import pt.isep.careplatform.repository.UserRepository;

@Component
public class PlatformAdminBootstrap implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String password;

    public PlatformAdminBootstrap(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${security.bootstrap-admin.email:}") String email,
            @Value("${security.bootstrap-admin.password:}") String password
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.password = password;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments arguments) {
        boolean hasEmail = email != null && !email.isBlank();
        boolean hasPassword = password != null && !password.isBlank();
        if (!hasEmail && !hasPassword) {
            return;
        }
        if (!hasEmail || !hasPassword) {
            throw new IllegalStateException(
                    "Configure both PLATFORM_ADMIN_EMAIL and PLATFORM_ADMIN_PASSWORD to bootstrap an administrator."
            );
        }

        String normalizedEmail = email.trim();
        User existingUser = userRepository.findByEmailIgnoreCase(normalizedEmail).orElse(null);
        if (existingUser != null) {
            if (existingUser.getRole() != UserRole.PLATFORM_ADMIN) {
                throw new IllegalStateException(
                        "The configured bootstrap account already exists with a different role."
                );
            }
            return;
        }

        userRepository.save(new User(
                normalizedEmail,
                passwordEncoder.encode(password),
                UserRole.PLATFORM_ADMIN
        ));
    }
}
