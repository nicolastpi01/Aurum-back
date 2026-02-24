package com.aurum.accounts.dto;

import java.time.LocalDateTime;

public record LedgerResponse ( 
	Long id,
	String entryType,
	double amount,
	String currency,
	String description,
	double balanceAfter,
	LocalDateTime createdAt
)	{}
