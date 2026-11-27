package pt.isep.careplatform.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.security.crypto.password.PasswordEncoder;
import pt.isep.careplatform.entity.User;
import pt.isep.careplatform.entity.UserRole;
import pt.isep.careplatform.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlatformAdminBootstrapTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void doesNothingWhenBootstrapCredentialsAreNotConfigured() {
        PlatformAdminBootstrap bootstrap = new PlatformAdminBootstrap(
                userRepository, passwordEncoder, "", ""
        );

        bootstrap.run(new DefaultApplicationArguments(new String[0]));

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void createsPlatformAdminWithEncodedPasswordWhenConfigured() {
        PlatformAdminBootstrap bootstrap = new PlatformAdminBootstrap(
                userRepository, passwordEncoder, " admin@example.com ", "strong-test-password"
        );
        when(userRepository.findByEmailIgnoreCase("admin@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("strong-test-password")).thenReturn("encoded-password");

        bootstrap.run(new DefaultApplicationArguments(new String[0]));

        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(savedUser.capture());
        assertEquals("admin@example.com", savedUser.getValue().getEmail());
        assertEquals("encoded-password", savedUser.getValue().getPasswordHash());
        assertEquals(UserRole.PLATFORM_ADMIN, savedUser.getValue().getRole());
    }

    @Test
    void doesNotChangeAnExistingPlatformAdmin() {
        PlatformAdminBootstrap bootstrap = new PlatformAdminBootstrap(
                userRepository, passwordEncoder, "admin@example.com", "strong-test-password"
        );
        when(userRepository.findByEmailIgnoreCase("admin@example.com"))
                .thenReturn(Optional.of(new User("admin@example.com", "existing-hash", UserRole.PLATFORM_ADMIN)));

        bootstrap.run(new DefaultApplicationArguments(new String[0]));

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void refusesToChangeAnExistingNonAdminRole() {
        PlatformAdminBootstrap bootstrap = new PlatformAdminBootstrap(
                userRepository, passwordEncoder, "sofia@example.com", "strong-test-password"
        );
        when(userRepository.findByEmailIgnoreCase("sofia@example.com"))
                .thenReturn(Optional.of(new User("sofia@example.com", "existing-hash", UserRole.PROVIDER)));

        assertThrows(IllegalStateException.class,
                () -> bootstrap.run(new DefaultApplicationArguments(new String[0])));
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void rejectsPartialBootstrapConfiguration() {
        PlatformAdminBootstrap bootstrap = new PlatformAdminBootstrap(
                userRepository, passwordEncoder, "admin@example.com", ""
        );

        assertThrows(IllegalStateException.class,
                () -> bootstrap.run(new DefaultApplicationArguments(new String[0])));
        verify(userRepository, never()).findByEmailIgnoreCase(any());
    }
}
