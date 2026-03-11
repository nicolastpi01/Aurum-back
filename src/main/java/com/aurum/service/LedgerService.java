package com.aurum.service;

import com.aurum.accounts.domain.Account;
import com.aurum.accounts.dto.LedgerResponse;
import com.aurum.accounts.repository.AccountRepository;
import com.aurum.accounts.repository.LedgerRepository;
import com.aurum.transfer.domain.LedgerEntries;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LedgerService {
	private final LedgerRepository ledgerRepository;
	private final AccountRepository accountRepository;
	
	public LedgerService(LedgerRepository ledgerRepository, AccountRepository accountRepository) {
		this.ledgerRepository = ledgerRepository;
		this.accountRepository = accountRepository;
	}
	
	public Page<LedgerResponse> getAccountMovements(Long accountId, Long userId, Pageable pageable) {
		accountRepository.findById(accountId)
		.filter(acc -> acc.getUser().getId().equals(userId))
		.orElseThrow(() -> new RuntimeException("Account not found or access denied"));
		
		return ledgerRepository.findByAccountIdOrderByCreatedAtDesc(accountId, pageable)
				.map(entry -> new LedgerResponse(
                        entry.getId(),
                        entry.getEntryType(),
                        entry.getAmount(),
                        entry.getCurrency(),
                        entry.getDescription(),
                        entry.getBalanceAfter(),
                        entry.getCreatedAt()
                ));
	}
	
	@Transactional
    public void createMovement(Account account, double amount, String type, String description) {
        LedgerEntries entry = new LedgerEntries();
        entry.setAccount(account);
        entry.setAmount(amount);
        entry.setEntryType(type);
        entry.setDescription(description);
        entry.setCurrency(account.getCurrency());
        entry.setBalanceAfter(account.getBalance()); 

        ledgerRepository.save(entry);
    }

}
