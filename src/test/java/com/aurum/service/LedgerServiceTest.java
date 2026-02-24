package com.aurum.service;

import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.aurum.accounts.domain.Account;
import com.aurum.accounts.dto.LedgerResponse;
import com.aurum.accounts.repository.AccountRepository;
import com.aurum.accounts.repository.LedgerRepository;
import com.aurum.transfer.domain.LedgerEntries;
import com.aurum.users.domain.User;

@ExtendWith(MockitoExtension.class)
public class LedgerServiceTest {
	
	@Mock
	private LedgerRepository ledgerRepository;
	
	@Mock
	private AccountRepository accountRepository;
	
	@InjectMocks
	private LedgerService ledgerService;
	
	private User userOwner;
	private Account account;
	
	@BeforeEach
	void setUp() {
		userOwner = new User();
      
        userOwner.setId(1L); 
        
        account = new Account();
        account.setId(101L);
        account.setUser(userOwner);
	}
	
	@Test
	void getAccountMovements_shouldReturnPage_WhenUserIsOwner() {
		
		Long accountId = 101L;
        Long userId = 1L;
		
		when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
		
		LedgerEntries entry = new LedgerEntries();
		entry.setAmount(1500.0);
		entry.setDescription("Test movement");
		
		when(ledgerRepository.findByAccountIdOrderByCreatedAtDesc(eq(accountId), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(entry)));
		
		
		Page<LedgerResponse> result = ledgerService.getAccountMovements(accountId, userId, Pageable.unpaged());
		
		assertNotNull(result);
		assertEquals(1, result.getContent().size());
		LedgerResponse response = result.getContent().get(0);
		assertEquals("Test movement", response.description());
		verify(ledgerRepository, times(1)).findByAccountIdOrderByCreatedAtDesc(eq(accountId), any());
	}
	
	@Test
    void getAccountMovements_shouldThrowException_WhenUserIsNotOwner() {
        Long accountId = 202L;
        Long hackerId = 1L;
        
        User realOwner = new User();
        
        account.setUser(realOwner);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        assertThrows(RuntimeException.class, () -> {
            ledgerService.getAccountMovements(accountId, hackerId, Pageable.unpaged());
        });

        verify(ledgerRepository, never()).findByAccountIdOrderByCreatedAtDesc(anyLong(), any());
    }
	
}
