package com.marisoft.booking.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public sealed interface AuthDto {
    record LoginRequest(
            @NotBlank(message = "El email es obligatorio")
            @Email(message = "Email inválido")
            String email,

            @NotBlank(message = "La contraseña es obligatoria")
            String password
    ) implements AuthDto {
    }

    record LoginResponse(
            String token,
            String message
    ) implements AuthDto {
    }
}
