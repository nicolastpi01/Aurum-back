package com.aurum.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aurum.accounts.domain.Account;
import com.aurum.accounts.repository.AccountRepository;
import com.aurum.auth.dto.AccountResponse;
import com.aurum.service.AccountService;
import com.aurum.users.domain.User;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
	
	@Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;
    
    /*
    @Test
    void getAccountsForUser_ShouldFilterByUserId() {
        // Arrange
        long userId = 1L;
        // Simulamos que el repo devuelve una lista con una cuenta
        when(accountRepository.findByUserId(userId)).thenReturn(List.of(new Account()));

        // Act
        List<AccountResponse> result = accountService.getAccountsForUser(userId);

        assertEquals(1, result.size());
        // Assert: Verificamos que filtre y no devuelva cuentas ajenas
        verify(accountRepository, times(1)).findByUserId(userId);
        verify(accountRepository, never()).findAll();
    }
    */
    
    void getAccountsForUser_ShouldFilterByUserId() {
        // Arrange
        long userId = 1L;
        
        // 1. Creamos el usuario dueño (el "User")
        User owner = new com.aurum.users.domain.User();
        // Importante: Si no tenés un constructor con ID, usá el setter si lo tenés, 
        // o simplemente confía en que el objeto existe, pero el Service pide el .getId()
        // Asegurate de que la entidad User tenga el getter getId()
        
        // 2. Creamos la cuenta y le seteamos el usuario
        Account account = new Account();
        // Aquí es vital que Account tenga el método setUser(user)
        account.setUser(owner); 

        // 3. Ahora el repo devuelve la cuenta "completa"
        when(accountRepository.findByUserId(userId)).thenReturn(List.of(account));

        // Act
        List<AccountResponse> result = accountService.getAccountsForUser(userId);

        // Assert
        assertEquals(1, result.size());
        verify(accountRepository, times(1)).findByUserId(userId);
    }

}
