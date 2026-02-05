package com.aurum.api.auth;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import com.aurum.users.domain.Role;
import com.aurum.users.domain.User;
import com.aurum.users.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SignInIntegrationTest {
	
	@Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void register_success_persistsUserInDatabase() throws Exception {
        String json = """
            {
                "email": "integration@aurum.com",
                "password": "password123"
            }
            """;

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated());

        User user = userRepository.findByMail("integration@aurum.com").orElseThrow();
        assertNotEquals("password123", user.getPassword_hash());
        assertTrue(passwordEncoder.matches("password123", user.getPassword_hash()));
    }

    @Test
    void register_duplicateEmail_returns409() throws Exception {
        User u = new User();
        u.setMail("duplicate@aurum.com");
        u.setPassword_hash("hash");
        u.setRole(Role.USER);
        userRepository.save(u);

        String json = """
            {
                "email": "duplicate@aurum.com",
                "password": "password123"
            }
            """;

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isConflict()); 
    }

}
