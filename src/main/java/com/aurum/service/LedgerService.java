package com.aurum.service;

import com.aurum.accounts.dto.LedgerResponse;
import com.aurum.accounts.repository.AccountRepository;
import com.aurum.accounts.repository.LedgerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

}
