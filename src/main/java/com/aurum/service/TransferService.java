package com.aurum.service;

import java.util.Date;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aurum.accounts.domain.Account;
import com.aurum.accounts.repository.AccountRepository;
import com.aurum.common.exception.BusinessRuleException;
import com.aurum.transfer.domain.Transfer;
import com.aurum.transfer.dto.TransferRequest;
import com.aurum.transfer.repository.TransferRepository;

@Service
public class TransferService {

    @Autowired private AccountRepository accountRepository;
    @Autowired private LedgerService ledgerService;
    @Autowired private TransferRepository transferRepository;

    @Transactional 
    public void transfer(TransferRequest request, String idempotencyKey) {
    	
    	// 1. Check de Idempotencia
        Optional<Transfer> existingTransfer = transferRepository.findByIdempotencyKey(idempotencyKey);
        if (existingTransfer.isPresent()) {
            return; 
        }
        
        // 2. Carga de cuentas
        Account source = accountRepository.findById(request.sourceAccountId())
                .orElseThrow(() -> new BusinessRuleException("Source account not found"));
        
        Account destination = accountRepository.findById(request.destinationAccountId())
                .orElseThrow(() -> new BusinessRuleException("Destination account not found"));

        // 3. Validación de Negocio
        if (source.getBalance() < request.amount()) {
            throw new BusinessRuleException("Insufficient funds in source account");
        }

        // 4. Operación aritmética
        source.setBalance(source.getBalance() - request.amount());
        destination.setBalance(destination.getBalance() + request.amount());

        // 5. Persistencia de saldos
        accountRepository.save(source);
        accountRepository.save(destination);

        // 6. Registro de la Transferencia (seteando todos los campos)
        Transfer transfer = new Transfer();
        transfer.setIdempotency_key(idempotencyKey);
        transfer.setFrom_account_id(source.getId());
        transfer.setTo_account_id(destination.getId());
        transfer.setAmount(request.amount());
        transfer.setCurrency(source.getCurrency());
        transfer.setStatus("COMPLETED");
        transfer.setDescription(request.description());
        transfer.setCreated_at(new Date());
        transfer.setUser_id(source.getUser().getId()); 

        transferRepository.save(transfer);

        // 7. Grabar asientos contables
        ledgerService.createMovement(source, request.amount(), "DEBIT", "Transfer to ID: " + destination.getId());
        ledgerService.createMovement(destination, request.amount(), "CREDIT", "Transfer from ID: " + source.getId());
    }
}
