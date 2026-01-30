package com.aurum.api.auth;


import com.aurum.users.domain.Role;
import com.aurum.users.domain.Status;
import com.aurum.users.domain.User;
import com.aurum.users.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ActiveProfiles("dev")
@SpringBootTest
@AutoConfigureMockMvc
class AuthLoginIntegrationTest {
	
	@Autowired PasswordEncoder passwordEncoder;
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @Autowired UserRepository userRepository;
    @Autowired JwtDecoder jwtDecoder;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        User u = new User();
        u.setMail("nico@aurum.com");
        u.setRole(Role.USER);
        u.setStatus(Status.ACTIVE);
        u.setPassword_hash(passwordEncoder.encode("123456"));
        userRepository.save(u);
    }

    @Test
    void login_ok_returnsSignedJwtWithClaims() throws Exception {
        String body = """
          {"email":"nico@aurum.com","password":"123456"}
        """;

        String json = mvc.perform(post("/api/v1/auth/login")
                .contentType("application/json")
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").isNotEmpty())
            .andReturn()
            .getResponse()
            .getContentAsString();

        String token = om.readTree(json).get("accessToken").asText();

        Jwt jwt = jwtDecoder.decode(token); // ✅ valida firma

        assert jwt.getSubject().equals("nico@aurum.com");
        assert jwt.getClaimAsString("role").equals("USER");
        assert jwt.getClaim("userId") != null;
    }

    @Test
    void login_invalidCredentials_returns401() throws Exception {
        String body = """
          {"email":"nico@aurum.com","password":"BAD"}
        """;

        mvc.perform(post("/api/v1/auth/login")
                .contentType("application/json")
                .content(body))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void login_invalidBody_returns400() throws Exception {
        String body = """
          {"email":"not-an-email","password":""}
        """;

        mvc.perform(post("/api/v1/auth/login")
                .contentType("application/json")
                .content(body))
            .andExpect(status().isBadRequest());
    }
}