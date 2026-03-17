package com.aurum.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.aurum.accounts.repository.AccountRepository;
import com.aurum.transfer.domain.Transfer;
import com.aurum.transfer.dto.TransferRequest;
import com.aurum.transfer.repository.TransferRepository;

@ExtendWith(MockitoExtension.class)
public class TransferServiceIdempotencyTest {
	@Mock private TransferRepository transferRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private LedgerService ledgerService;
    @InjectMocks private TransferService transferService;

    @Test
    @DisplayName("Should return early if idempotency key already exists")
    void transfer_ExistingKey_DoesNothing() {
        // Arrange
        String key = "unique-key-123";
        TransferRequest request = new TransferRequest(1L, 2L, 100.0, "Test");
        when(transferRepository.findByIdempotencyKey(key)).thenReturn(Optional.of(new Transfer()));

        // Act
        transferService.transfer(request, key);

        // Assert
        verify(accountRepository, never()).findById(anyLong());
        verify(ledgerService, never()).createMovement(any(), anyDouble(), anyString(), anyString());
        verify(transferRepository, never()).save(any());
    }

}
