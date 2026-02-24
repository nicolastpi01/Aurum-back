package com.aurum.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aurum.accounts.repository.AccountRepository;
import com.aurum.auth.dto.AccountResponse;

@Service
public class AccountService {
	@Autowired
    private final AccountRepository accountRepository;

	public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    
    public List<AccountResponse> getAccountsForUser(long userId) {
        return accountRepository.findByUserId(userId)
                .stream()
                .map(acc -> new AccountResponse(
                        acc.getId(),
                        acc.getCurrency(),
                        acc.getBalance(),
                        acc.getStatus(),
                        acc.getUser().getId() 
                )).toList();
    }

}
