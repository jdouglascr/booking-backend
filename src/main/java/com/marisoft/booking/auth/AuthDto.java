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
            String accessToken,
            String refreshToken,
            String message
    ) implements AuthDto {
    }

    record RefreshTokenRequest(
            @NotBlank(message = "El refresh token es obligatorio")
            String refreshToken
    ) implements AuthDto {
    }

    record RefreshTokenResponse(
            String accessToken,
            String refreshToken
    ) implements AuthDto {
    }
}