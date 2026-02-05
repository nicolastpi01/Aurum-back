package com.aurum.account.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import com.aurum.auth.dto.AccountResponse;
import com.aurum.service.AccountService;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {
	
	private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getMyAccounts(@AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        
        List<AccountResponse> accounts = accountService.getAccountsForUser(userId);
        return ResponseEntity.ok(accounts);
    }

}
