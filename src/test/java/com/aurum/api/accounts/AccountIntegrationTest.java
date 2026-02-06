package com.aurum.api.accounts;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AccountIntegrationTest {
	
	@Autowired
    private MockMvc mockMvc;

	
	@Test
	@Sql(scripts = "/setup-accounts.sql", 
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "DELETE FROM accounts; DELETE FROM users;", 
    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAccounts_shouldShowOnlyOwnData() throws Exception {
        mockMvc.perform(get("/api/v1/accounts")
               .with(jwt().jwt(j -> j.claim("userId", 1L)))) 
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(2))
               .andExpect(jsonPath("$[0].userId").value(1))
               .andExpect(jsonPath("$[0].id").value(101))
               .andExpect(jsonPath("$[1].id").value(102));
    }

}
