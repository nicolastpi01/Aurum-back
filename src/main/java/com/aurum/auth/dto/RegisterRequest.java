package com.aurum.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
	    @NotBlank(message = "El email es obligatorio")
	    @Email(message = "Formato de email inválido")
	    String email,

	    @NotBlank(message = "La password es obligatoria")
	    @Size(min = 8, message = "La password debe tener al menos 8 caracteres")
	    String password
	) {}