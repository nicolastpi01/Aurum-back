package com.aurum.auth.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.aurum.auth.exception.InvalidCredentialsException;

@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> invalidCredentials() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}