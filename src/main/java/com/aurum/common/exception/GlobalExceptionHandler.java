package com.aurum.common.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.aurum.auth.exception.EmailAlreadyExistsException;
import com.aurum.common.api.ApiError;
import com.aurum.common.api.ApiErrorCode;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleConflict(EmailAlreadyExistsException ex) {
		ApiError error = new ApiError(ApiErrorCode.BUSINESS_RULE_VIOLATION, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> details = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(f -> details.put(f.getField(), f.getDefaultMessage()));
        //return ResponseEntity.badRequest().body(errors);
        ApiError error = new ApiError(ApiErrorCode.VALIDATION_ERROR, "Invalid request parameters", details);
        return ResponseEntity.badRequest().body(error);
    }

    // --- MOVIMIENTOS ---
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleRuntimeException(RuntimeException ex) {
        // Si el error es de seguridad de cuenta, devolvemos 403 Forbidden
        if (ex.getMessage().contains("access denied") || ex.getMessage().contains("not found")) {
        	
        	ApiError error = new ApiError(
                    ApiErrorCode.ACCESS_DENIED, 
                    ex.getMessage()
                );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }
        ApiError error = new ApiError(
                ApiErrorCode.INTERNAL_ERROR, 
                "An unexpected error occurred on the server"
            );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

}
