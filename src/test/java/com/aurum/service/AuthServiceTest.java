package com.aurum.service;

import com.aurum.auth.application.AuthService;
import com.aurum.auth.dto.RegisterRequest;
import com.aurum.auth.exception.EmailAlreadyExistsException;
import com.aurum.auth.exception.InvalidCredentialsException;
import com.aurum.users.domain.Role;
import com.aurum.users.domain.User;
import com.aurum.users.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {
	
	private UserRepository repo;
    private PasswordEncoder encoder;
    private JwtEncoder jwtEncoder;
    private AuthService svc;
    private User u;
	
	
	@BeforeEach
    void setUp() {
        repo = mock(UserRepository.class);
        encoder = mock(PasswordEncoder.class);
        jwtEncoder = mock(JwtEncoder.class);
        
        u = new User();
        u.setMail("nico@aurum.com");
        u.setPassword_hash("HASH");
        u.setRole(Role.USER);
        when(repo.findByMail("nico@aurum.com")).thenReturn(Optional.of(u));
        
        svc = new AuthService(repo, encoder, jwtEncoder, "aurum", 15);
    }

    @Test
    void login_ok_returnsToken() {

        when(encoder.matches("123456", "HASH")).thenReturn(true);

        Jwt jwt = Jwt.withTokenValue("TOKEN").header("alg","HS256").claim("sub","nico@aurum.com").build();
        when(jwtEncoder.encode(any())).thenReturn(jwt);


        String token = svc.login("nico@aurum.com", "123456");
        assertEquals("TOKEN", token);
    }

    @Test
    void login_invalidPassword_throws() {

        when(encoder.matches("BAD", "HASH")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> svc.login("nico@aurum.com", "BAD"));
    }
    
    @Test
    void register_ok_shouldHashPasswordAndSaveUser() {
    	
        RegisterRequest req = new RegisterRequest("nuevo@aurum.com", "pass12345");
        when(repo.findByMail(req.email())).thenReturn(Optional.empty());
        when(encoder.encode("pass12345")).thenReturn("ENCODED_PASS");

        // Act
        svc.register(req);

     
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(repo).save(userCaptor.capture());
        
        User savedUser = userCaptor.getValue();
        assertEquals("nuevo@aurum.com", savedUser.getMail());
        assertEquals("ENCODED_PASS", savedUser.getPassword_hash());
        assertEquals(Role.USER, savedUser.getRole());
    }

    @Test
    void register_existingEmail_shouldThrowException() {
        // Arrange
        RegisterRequest req = new RegisterRequest("yaexiste@aurum.com", "pass123");
        when(repo.findByMail("yaexiste@aurum.com")).thenReturn(Optional.of(new User()));

        // Act & Assert
        assertThrows(EmailAlreadyExistsException.class, () -> svc.register(req));
        verify(repo, never()).save(any());
    }
}