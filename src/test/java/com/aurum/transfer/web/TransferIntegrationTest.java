package com.aurum.transfer.web;

import com.aurum.accounts.domain.Account;
import com.aurum.accounts.repository.AccountRepository;
import com.aurum.accounts.repository.LedgerRepository;
import com.aurum.transfer.dto.TransferRequest;
import com.aurum.users.domain.Role;
import com.aurum.users.domain.User;
import com.aurum.users.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class TransferIntegrationTest {
	@Autowired 
	private MockMvc mockMvc;
    @Autowired 
    private AccountRepository accountRepository;
    @Autowired 
    private LedgerRepository ledgerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired 
    private ObjectMapper objectMapper;

    private Long sourceId;
    private Long destId;
    
    @BeforeEach
    void setup() {
        ledgerRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();

        // 1. Creamos un usuario para que sea dueño de las cuentas
        User owner = new User();
        owner.setMail("test@aurum.com");
        owner.setPassword_hash("hash");
        owner.setRole(Role.USER);
        userRepository.save(owner);
        
        Account source = new Account();
        source.setUser(owner);
        source.setBalance(1000.0);
        source.setCurrency("ARS");
        source.setStatus("ACTIVE");
        sourceId = accountRepository.save(source).getId();

        Account dest = new Account();
        dest.setUser(owner);
        dest.setBalance(100.0);
        dest.setCurrency("ARS");
        dest.setStatus("ACTIVE");
        destId = accountRepository.save(dest).getId();
    }
    
    @Test
    @WithMockUser // Simula usuario autenticado para saltar Spring Security
    @DisplayName("POST /transfers should complete full cycle: update balances and create 2 ledger entries")
    void shouldPerformFullTransferSuccessfully() throws Exception {
        TransferRequest request = new TransferRequest(sourceId, destId, 200.0, "Test Integration");

        mockMvc.perform(post("/api/v1/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // 1. Verificar Saldos
        assertThat(accountRepository.findById(sourceId).get().getBalance()).isEqualTo(800.0);
        assertThat(accountRepository.findById(destId).get().getBalance()).isEqualTo(300.0);

        // 2. Verificar que se crearon exactamente 2 ledger entries
        assertThat(ledgerRepository.count()).isEqualTo(2);
        
        // 3. Verificar que no hay duplicación parcial (si hay 2, están el par completo)
        var entries = ledgerRepository.findAll();
        assertThat(entries).extracting("entryType").containsExactlyInAnyOrder("DEBIT", "CREDIT");
    }
    
    @Test
    @WithMockUser
    @DisplayName("POST /transfers with insufficient funds should return 422 and NOT create any ledger entries")
    void shouldNotCreatePartialEntriesOnFailure() throws Exception {
        TransferRequest request = new TransferRequest(sourceId, destId, 5000.0, "Fallo fondos");

        mockMvc.perform(post("/api/v1/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());

        // Verificar que NADA cambió en la DB
        assertThat(accountRepository.findById(sourceId).get().getBalance()).isEqualTo(1000.0);
        assertThat(ledgerRepository.count()).isZero();
    }
    

}
