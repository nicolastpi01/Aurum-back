package com.aurum.auth;

import com.aurum.domain.User;
import com.aurum.domain.UserRepository;
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
}