package com.aurum.common.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ApiErrorTest {
	
	@Test
	@DisplayName("Should create ApiError with current timestamp and null details")
	void shouldCreateSimpleApiError() {
		String message = "Invalid request";
		ApiErrorCode code = ApiErrorCode.VALIDATION_ERROR;
		
		ApiError apiError = new ApiError(code, message);
		
		assertThat(apiError.code()).isEqualTo(code);
		assertThat(apiError.message()).isEqualTo(message);
		assertThat(apiError.timestamp()).isBeforeOrEqualTo(LocalDateTime.now());
		assertThat(apiError.details()).isNull();
	}
	
	@Test
	@DisplayName("Should create ApiError including validation details")
	void shouldCreateApiErrorWithDetails( ) {
		Map<String, String> details = Map.of("email", "must be a valid email address");
		ApiError apiError = new ApiError(
			ApiErrorCode.VALIDATION_ERROR,
			"Validation failed",
			details
		);
		assertThat(apiError.details())
		.hasSize(1)
		.containsEntry("email", "must be a valid email address");
	}

}
