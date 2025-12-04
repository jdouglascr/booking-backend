package com.marisoft.booking.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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

    record CurrentUserResponse(
            Integer id,
            String email,
            String fullName,
            String firstName,
            String lastName,
            String phone,
            String role,
            Boolean isActive
    ) implements AuthDto {
    }

    record UpdateProfileRequest(
            @NotBlank(message = "El nombre es obligatorio")
            @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
            String firstName,

            @NotBlank(message = "El apellido es obligatorio")
            @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
            String lastName,

            @NotBlank(message = "El email es obligatorio")
            @Email(message = "Email inválido")
            String email,

            @NotBlank(message = "El teléfono es obligatorio")
            @Size(min = 12, max = 12, message = "El teléfono debe tener el formato +56XXXXXXXXX")
            String phone,

            @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
            String password
    ) implements AuthDto {
    }
}