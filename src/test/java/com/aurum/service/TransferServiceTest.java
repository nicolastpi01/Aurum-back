package com.aurum.service;

import com.aurum.accounts.domain.Account;
import com.aurum.accounts.repository.AccountRepository;
import com.aurum.common.exception.BusinessRuleException; 
import com.aurum.transfer.dto.TransferRequest;
import com.aurum.transfer.repository.TransferRepository;
import com.aurum.users.domain.User;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock private AccountRepository accountRepository;
    @Mock private LedgerService ledgerService;
    @Mock private TransferRepository transferRepository;
    @InjectMocks private TransferService transferService;

    @Test
    @DisplayName("Should throw exception when source account has insufficient funds")
    void transfer_InsufficientFunds_ThrowsException() {
        // Arrange
        Long sourceId = 1L;
        Long destId = 2L;
        double amount = 100.00;
        
        Account sourceAcc = new Account();
        sourceAcc.setId(sourceId);
        double initialBalance = 50.00;
        sourceAcc.setBalance(initialBalance);

        when(transferRepository.findByIdempotencyKey(anyString())).thenReturn(Optional.empty());
        when(accountRepository.findById(sourceId)).thenReturn(Optional.of(sourceAcc));
        when(accountRepository.findById(destId)).thenReturn(Optional.of(new Account()));

        TransferRequest request = new TransferRequest(sourceId, destId, amount, "Test transfer");

        assertThrows(BusinessRuleException.class, () -> transferService.transfer(request, "AHASHD123"));
        
        // Verificamos que nunca se llamó al ledger porque falló antes
        verify(ledgerService, never()).createMovement(any(Account.class), anyDouble(), anyString(), anyString());
    }
    
    @Test
    @DisplayName("Should successfully transfer funds between two valid accounts")
    void transfer_Success_CreatesDebitAndCreditMovements() {
        Long sourceId = 1L;
        Long destId = 2L;
        double amount = 100.00;
        
        User mockUser = new User();
        mockUser.setId(10L);
        
        Account sourceAcc = new Account();
        sourceAcc.setId(sourceId);
        sourceAcc.setBalance(500.00);
        sourceAcc.setUser(mockUser);
        
        Account destAcc = new Account();
        destAcc.setId(destId);
        destAcc.setBalance(50.00);

        when(transferRepository.findByIdempotencyKey(anyString())).thenReturn(Optional.empty());
        when(accountRepository.findById(sourceId)).thenReturn(Optional.of(sourceAcc));
        when(accountRepository.findById(destId)).thenReturn(Optional.of(destAcc));

        TransferRequest request = new TransferRequest(sourceId, destId, amount, "Pago cena");

        transferService.transfer(request,"AHASHD123");

        // Assert
        // 1. Verificamos que se actualizaron los saldos en los objetos
        assertThat(sourceAcc.getBalance()).isEqualTo(400.00);
        assertThat(destAcc.getBalance()).isEqualTo(150.00);

        // 2. Verificamos que se llamó al LedgerService para registrar AMBOS movimientos
        // Verificamos el Débito (Salida de dinero)
        verify(ledgerService, times(1)).createMovement(eq(sourceAcc), eq(amount), eq("DEBIT"), anyString());

        // Verificamos el Crédito (Entrada de dinero)
        verify(ledgerService, times(1)).createMovement(eq(destAcc), eq(amount), eq("CREDIT"), anyString());
        
        // 3. Verificamos que se guardaron los cambios en las cuentas
        verify(accountRepository, times(1)).save(sourceAcc);
        verify(accountRepository, times(1)).save(destAcc);
    }
}