package com.aurum.api.accounts;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
 @ActiveProfiles("test")
public class MovementIntegrationTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	
	@Test
    @Sql("/setup-ledger.sql") 
    void getMovements_shouldReturnPagedDataForOwner() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/1/movements")
                .with(jwt().jwt(j -> j.claim("userId", 1L))) // Dueño
                .param("page", "0")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3)) // Valida el tamaño de página
                .andExpect(jsonPath("$.totalElements").value(3)) // Valida el total en DB
        		.andExpect(jsonPath("$.content[0].description").exists());
    }
	
	@Test
	@Sql("/setup-ledger.sql")
	void getMovements_shouldReturn404WhenAccesingOtherUserAccount() throws Exception {
		mockMvc.perform(get("/api/v1/accounts/1/movements")
				.with(jwt().jwt(j -> j.claim("userId", 2L))) // Usuario diferente
				.param("page", "0")
				.param("size", "5"))
				.andExpect(status().isForbidden());
	}

}
