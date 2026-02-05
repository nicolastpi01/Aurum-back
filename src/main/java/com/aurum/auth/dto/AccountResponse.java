package com.aurum.auth.dto;

public record AccountResponse(
	    long id,
	    String currency,
	    double balance,
	    String status,
	    Long userId) {}