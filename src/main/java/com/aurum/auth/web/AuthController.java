package com.aurum.auth.web;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.aurum.auth.application.AuthService;
import com.aurum.auth.dto.LoginRequest;
import com.aurum.auth.dto.LoginResponse;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
	
	 private final AuthService authService;

	    public AuthController(AuthService authService) {
	        this.authService = authService;
	    }

	    @PostMapping("/login")
	    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
	        String token = authService.login(req.email(), req.password());
	        return ResponseEntity.ok(new LoginResponse(token, "Bearer"));
	    }

}
