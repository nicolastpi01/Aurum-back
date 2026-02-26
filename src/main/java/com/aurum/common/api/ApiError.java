package com.aurum.common.api;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiError (
	ApiErrorCode code,
	String message,
	LocalDateTime timestamp,
	Map<String, String> details
) {
	public ApiError(ApiErrorCode code, String message) {
		this(code, message, LocalDateTime.now(), null);
	}
	
	public ApiError(ApiErrorCode code, String message, Map<String, String> details) {
		this(code, message, LocalDateTime.now(), details);
	}
}

