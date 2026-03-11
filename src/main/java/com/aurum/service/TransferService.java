package com.aurum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aurum.accounts.domain.Account;
import com.aurum.accounts.repository.AccountRepository;
import com.aurum.common.exception.BusinessRuleException;
import com.aurum.transfer.dto.TransferRequest;

@Service
public class TransferService {

    @Autowired private AccountRepository accountRepository;
    @Autowired private LedgerService ledgerService;

    @Transactional 
    public void transfer(TransferRequest request) {
        Account source = accountRepository.findById(request.sourceAccountId())
                .orElseThrow(() -> new BusinessRuleException("Source account not found"));
        
        Account destination = accountRepository.findById(request.destinationAccountId())
                .orElseThrow(() -> new BusinessRuleException("Destination account not found"));

        if (source.getBalance() < request.amount()) {
            throw new BusinessRuleException("Insufficient funds in source account");
        }

        // Lógica de transferencia
        source.setBalance(source.getBalance() - request.amount());
        destination.setBalance(destination.getBalance() + request.amount());

        // Guardar saldos actualizados
        accountRepository.save(source);
        accountRepository.save(destination);

        // 3. Grabar los dos asientos contables
        ledgerService.createMovement(source, request.amount(), "DEBIT", "DEBIT" + "Transfer to ID: " + destination.getId());
        ledgerService.createMovement(destination, request.amount(), "CREDIT", "CREDIT" + "Transfer from ID: " + source.getId());
    }
}
