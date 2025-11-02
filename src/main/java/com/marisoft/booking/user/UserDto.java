package com.marisoft.booking.user;

import com.marisoft.booking.shared.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public sealed interface UserDto {
    record CreateRequest(
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

            @NotBlank(message = "La contraseña es obligatoria")
            @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
            String password,

            @NotNull(message = "El rol es obligatorio")
            Role role,

            @NotNull(message = "El estado es obligatorio")
            Boolean isActive
    ) implements UserDto {
    }

    record UpdateRequest(
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

            @NotNull(message = "El rol es obligatorio")
            Role role,

            @NotNull(message = "El estado es obligatorio")
            Boolean isActive,

            String password
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