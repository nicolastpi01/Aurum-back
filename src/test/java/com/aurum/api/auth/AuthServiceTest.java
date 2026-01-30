package com.aurum.api.auth;

import com.aurum.auth.application.AuthService;
import com.aurum.auth.exception.InvalidCredentialsException;
import com.aurum.users.domain.Role;
import com.aurum.users.domain.User;
import com.aurum.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Test
    void login_ok_returnsToken() {
        UserRepository repo = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        JwtEncoder jwtEncoder = mock(JwtEncoder.class);

        User u = new User();
        u.setMail("nico@aurum.com");
        u.setPassword_hash("HASH");
        u.setRole(Role.USER);
        when(repo.findByMail("nico@aurum.com")).thenReturn(Optional.of(u));
        when(encoder.matches("123456", "HASH")).thenReturn(true);

        Jwt jwt = Jwt.withTokenValue("TOKEN").header("alg","HS256").claim("sub","nico@aurum.com").build();
        when(jwtEncoder.encode(any())).thenReturn(jwt);

        AuthService svc = new AuthService(repo, encoder, jwtEncoder, "aurum", 15);

        String token = svc.login("nico@aurum.com", "123456");
        assertEquals("TOKEN", token);
    }

    @Test
    void login_invalidPassword_throws() {
        UserRepository repo = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        JwtEncoder jwtEncoder = mock(JwtEncoder.class);

        User u = new User();
        u.setMail("nico@aurum.com");
        u.setPassword_hash("HASH");
        when(repo.findByMail("nico@aurum.com")).thenReturn(Optional.of(u));
        when(encoder.matches("BAD", "HASH")).thenReturn(false);

        AuthService svc = new AuthService(repo, encoder, jwtEncoder, "aurum", 15);

        assertThrows(InvalidCredentialsException.class, () -> svc.login("nico@aurum.com", "BAD"));
    }
}