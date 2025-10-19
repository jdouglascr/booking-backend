package com.marisoft.booking.user;

import com.marisoft.booking.shared.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public sealed interface UserDto {
    record CreateRequest(
            @NotBlank(message = "El nombre es obligatorio")
            @Size(min = 2, max = 100)
            String firstName,

            @NotBlank(message = "El apellido es obligatorio")
            @Size(min = 2, max = 100)
            String lastName,

            @NotBlank(message = "El email es obligatorio")
            @Email(message = "Email inválido")
            String email,

            String phone,

            @NotBlank(message = "La contraseña es obligatoria")
            @Size(min = 6)
            String password,

            Role role
    ) implements UserDto {
    }

    record UpdateRequest(
            @NotBlank(message = "El nombre es obligatorio")
            @Size(min = 2, max = 100)
            String firstName,

            @NotBlank(message = "El apellido es obligatorio")
            @Size(min = 2, max = 100)
            String lastName,

            @NotBlank(message = "El email es obligatorio")
            @Email(message = "Email inválido")
            String email,

            String phone,

            Role role
    ) implements UserDto {
    }

    record Response(
            Integer id,
            String firstName,
            String lastName,
            String email,
            String phone,
            String role,
            Boolean isActive,
            LocalDateTime lastLogin,
            LocalDateTime createdAt
    ) implements UserDto {
        public static Response fromEntity(User user) {
            return new Response(
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getRole().getDisplayName(),
                    user.getIsActive(),
                    user.getLastLogin(),
                    user.getCreatedAt()
            );
        }
    }
}