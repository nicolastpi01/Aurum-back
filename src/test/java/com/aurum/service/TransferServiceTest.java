package com.aurum.service;

import com.aurum.accounts.domain.Account;
import com.aurum.accounts.repository.AccountRepository;
import com.aurum.common.exception.BusinessRuleException; 
import com.aurum.transfer.dto.TransferRequest; 
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

        when(accountRepository.findById(sourceId)).thenReturn(Optional.of(sourceAcc));
        when(accountRepository.findById(destId)).thenReturn(Optional.of(new Account()));

        TransferRequest request = new TransferRequest(sourceId, destId, amount, "Test transfer");

        // Act & Assert
        assertThrows(BusinessRuleException.class, () -> transferService.transfer(request));
        
        // Verificamos que NUNCA se llamó al ledger porque falló antes
        verify(ledgerService, never()).createMovement(any(), any(), any(), any());
    }
    
    @Test
    @DisplayName("Should successfully transfer funds between two valid accounts")
    void transfer_Success_CreatesDebitAndCreditMovements() {
        // Arrange
        Long sourceId = 1L;
        Long destId = 2L;
        double amount = 100.00;
        
        Account sourceAcc = new Account();
        sourceAcc.setId(sourceId);
        sourceAcc.setBalance(500.00); // Saldo suficiente

        Account destAcc = new Account();
        destAcc.setId(destId);
        destAcc.setBalance(50.00);

        when(accountRepository.findById(sourceId)).thenReturn(Optional.of(sourceAcc));
        when(accountRepository.findById(destId)).thenReturn(Optional.of(destAcc));

        TransferRequest request = new TransferRequest(sourceId, destId, amount, "Pago cena");

        // Act
        transferService.transfer(request);

        // Assert
        // 1. Verificamos que se actualizaron los saldos en los objetos (Ojo: el service debe hacer setBalance)
        assertThat(sourceAcc.getBalance()).isEqualTo(400.00);
        assertThat(destAcc.getBalance()).isEqualTo(150.00);

        // 2. Verificamos que se llamó al LedgerService para registrar AMBOS movimientos
        // Verificamos el Débito (Salida de dinero)
        verify(ledgerService, times(1)).createMovement(
            eq(sourceAcc),          // Ahora pasamos el objeto Account
            eq(amount),             // El monto (positivo, el tipo define que es resta)
            eq("DEBIT"),            // El tipo de entrada
            anyString()             // La descripción
        );

        // Verificamos el Crédito (Entrada de dinero)
        verify(ledgerService, times(1)).createMovement(
            eq(destAcc),            // Ahora pasamos el objeto Account
            eq(amount),             // El monto
            eq("CREDIT"),           // El tipo de entrada
            anyString()             // La descripción
        );
        
        // 3. Verificamos que se guardaron los cambios en las cuentas
        verify(accountRepository, times(1)).save(sourceAcc);
        verify(accountRepository, times(1)).save(destAcc);
    }
}