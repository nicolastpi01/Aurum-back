package com.aurum.account.web;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import com.aurum.accounts.dto.LedgerResponse;
import com.aurum.auth.dto.AccountResponse;
import com.aurum.service.AccountService;
import com.aurum.service.LedgerService;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {
	
	private final AccountService accountService;
	private final LedgerService ledgerService;

    public AccountController(AccountService accountService, LedgerService LedgerService) {
        this.accountService = accountService;
        this.ledgerService = LedgerService;
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getMyAccounts(@AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        
        List<AccountResponse> accounts = accountService.getAccountsForUser(userId);
        return ResponseEntity.ok(accounts);
    }
    
    
    @GetMapping("/{accountId}/movements")
    public ResponseEntity<Page<LedgerResponse>> getAccountMovements(
    		@PathVariable("accountId") Long accountId, 
    		@AuthenticationPrincipal Jwt jwt, 
    		@PageableDefault(size = 10) Pageable pageable) {
    	
    	Long userId = jwt.getClaim("userId");
    	
    	Page<LedgerResponse> movements = ledgerService.getAccountMovements(accountId, userId, pageable);
    	
    	return ResponseEntity.ok(movements);
    }

}
