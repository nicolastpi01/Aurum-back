package com.aurum.auth.application;

import com.aurum.auth.dto.RegisterRequest;
import com.aurum.auth.exception.EmailAlreadyExistsException;
import com.aurum.auth.exception.InvalidCredentialsException;
import com.aurum.users.domain.Role;
import com.aurum.users.domain.User;
import com.aurum.users.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    private final String issuer;
    private final long ttlMinutes;

    public AuthService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        JwtEncoder jwtEncoder,
        @Value("${aurum.security.jwt.issuer}") String issuer,
        @Value("${aurum.security.jwt.ttl-minutes}") long ttlMinutes
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.ttlMinutes = ttlMinutes;
    }

    public String login(String email, String rawPassword) {
        User user = userRepository.findByMail(email)
            .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(rawPassword, user.getPassword_hash())) {
            throw new InvalidCredentialsException();
        }

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer(issuer)
            .issuedAt(now)
            .expiresAt(now.plus(ttlMinutes, ChronoUnit.MINUTES))
            .subject(user.getMail())              // sub=email
            .claim("userId", user.getId())
            .claim("role", user.getRole().name())
            .build();
        
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
    
    public void register(RegisterRequest req) {
        // 1. Validar si el email ya existe (Criterio 409)
        if (userRepository.findByMail(req.email()).isPresent()) {
            throw new EmailAlreadyExistsException("El email " + req.email() + " ya está registrado.");
        }

        // 2. Crear y persistir usuario (Criterio Hasheo + Rol USER)
        User user = new User();
        user.setMail(req.email());
        user.setPassword_hash(passwordEncoder.encode(req.password()));
        user.setRole(Role.USER);

        userRepository.save(user);
    }
}