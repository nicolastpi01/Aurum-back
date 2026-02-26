package com.aurum.api.auth;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import com.aurum.accounts.repository.AccountRepository;
import com.aurum.accounts.repository.LedgerRepository;
import com.aurum.common.api.ApiErrorCode;
import com.aurum.users.domain.Role;
import com.aurum.users.domain.User;
import com.aurum.users.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ApiErrorShapeIntegrationTest {
	
	 @Autowired
	 private MockMvc mockMvc;
	 
	 @Autowired
	 private UserRepository userRepository;
	 @Autowired 
	 private AccountRepository accountRepository; 
	 @Autowired 
	 private LedgerRepository ledgerRepository;   
	 
	 @BeforeEach
	 void setUp() {
		 if (ledgerRepository != null) ledgerRepository.deleteAll(); 
		 	accountRepository.deleteAll();
		    userRepository.deleteAll();
	 }
	 
	 @Test
	 void shouldReturnUniformErrorShapeForValidationError_400() throws Exception {
		 
		 String invalidJson = """
		            { "email": "esto-no-es-un-email", "password": "" }
			        """;
		 mockMvc.perform(post("/api/v1/auth/register")
				 .contentType(MediaType.APPLICATION_JSON)
				 .content(invalidJson))
				 .andExpect(status().isBadRequest())
				 .andExpect(jsonPath("$.code").value(ApiErrorCode.VALIDATION_ERROR.name()))
				 .andExpect(jsonPath("$.message").isNotEmpty())
				 .andExpect(jsonPath("$.timestamp").exists())
				 .andExpect(jsonPath("$.details").isMap())
				 .andExpect(jsonPath("$.details.email").exists());
		 
	 }
	 
	 @Test
	 void shouldReturnUniformErrorShapeForBusinessRule_422() throws Exception {
		 User u = new User();
		 u.setMail("duplicate@Aurum.com");
		 u.setPassword_hash("hash");
		 u.setRole(Role.USER);
		 userRepository.save(u);
		 
		 String json = """
		            { "email": "duplicate@Aurum.com", "password": "password123" }
		            
		 		""";
		 
		 mockMvc.perform(post("/api/v1/auth/register")
				 .contentType(MediaType.APPLICATION_JSON)
				 .content(json))
				 .andExpect(status().isUnprocessableEntity()) // 422
				 .andExpect(jsonPath("$.code").value(ApiErrorCode.BUSINESS_RULE_VIOLATION.name()))
				 .andExpect(jsonPath("$.message", containsString("ya está registrado")))
				 .andExpect(jsonPath("$.timestamp").exists());
				 
	 }

}
