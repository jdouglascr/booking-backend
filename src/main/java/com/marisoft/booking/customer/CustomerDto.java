package com.marisoft.booking.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public sealed interface CustomerDto {

    record UpsertRequest(
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
            @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
            String phone
    ) implements CustomerDto {
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
            @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
            String phone
    ) implements CustomerDto {
    }

    record Response(
            Integer id,
            String firstName,
            String lastName,
            String email,
            String phone,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) implements CustomerDto {
        public static Response fromEntity(Customer customer) {
            return new Response(
                    customer.getId(),
                    customer.getFirstName(),
                    customer.getLastName(),
                    customer.getEmail(),
                    customer.getPhone(),
                    customer.getCreatedAt(),
                    customer.getUpdatedAt()
            );
        }
    }
}