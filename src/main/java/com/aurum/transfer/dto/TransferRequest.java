package com.aurum.transfer.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransferRequest(
	@NotNull Long sourceAccountId,
	@NotNull Long destinationAccountId,
	@Positive double amount,
	String description
) {}