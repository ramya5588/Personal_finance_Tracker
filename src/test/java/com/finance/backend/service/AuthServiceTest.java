package com.finance.backend.service;

import com.finance.backend.dto.AuthResponse;
import com.finance.backend.dto.LoginRequest;
import com.finance.backend.dto.RegisterRequest;
import com.finance.backend.entity.AppUser;
import com.finance.backend.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Test
    void registrationNormalizesEmailAndHashesPassword() {
        AuthService authService = service();
        when(passwordEncoder.encode("securePass1")).thenReturn("hashed-password");
        when(userRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(tokenService.createToken(any(AppUser.class))).thenReturn("signed-token");
        when(tokenService.expirationSeconds()).thenReturn(28_800L);

        AuthResponse response = authService.register(
                new RegisterRequest(" Ramya ", " Ramya@Example.COM ", "securePass1")
        );

        ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("Ramya", userCaptor.getValue().getName());
        assertEquals("ramya@example.com", userCaptor.getValue().getEmail());
        assertEquals("hashed-password", userCaptor.getValue().getPasswordHash());
        assertEquals("signed-token", response.token());
    }

    @Test
    void registrationRejectsDuplicateEmail() {
        AuthService authService = service();
        when(userRepository.existsByEmail("ramya@example.com")).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> authService.register(
                new RegisterRequest("Ramya", "ramya@example.com", "securePass1")
        ));
    }

    @Test
    void loginAuthenticatesBeforeIssuingToken() {
        AuthService authService = service();
        AppUser user = new AppUser("Ramya", "ramya@example.com", "hash");
        when(authenticationManager.authenticate(any(Authentication.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findByEmail("ramya@example.com")).thenReturn(Optional.of(user));
        when(tokenService.createToken(user)).thenReturn("signed-token");

        AuthResponse response = authService.login(new LoginRequest("RAMYA@example.com", "securePass1"));

        verify(authenticationManager).authenticate(any(Authentication.class));
        assertEquals("signed-token", response.token());
        assertEquals("ramya@example.com", response.email());
    }

    private AuthService service() {
        return new AuthService(userRepository, passwordEncoder, authenticationManager, tokenService);
    }
}
