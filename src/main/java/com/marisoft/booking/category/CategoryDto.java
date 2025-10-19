package com.marisoft.booking.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public sealed interface CategoryDto {

    record CreateRequest(
            @NotBlank(message = "El nombre es obligatorio")
            @Size(min = 2, max = 255, message = "El nombre debe tener entre 2 y 255 caracteres")
            String name
    ) implements CategoryDto {
    }

    record UpdateRequest(
            @NotBlank(message = "El nombre es obligatorio")
            @Size(min = 2, max = 255, message = "El nombre debe tener entre 2 y 255 caracteres")
            String name
    ) implements CategoryDto {
    }

    record Response(
            Integer id,
            String name,
            LocalDateTime createdAt
    ) implements CategoryDto {
        public static Response fromEntity(Category category) {
            return new Response(
                    category.getId(),
                    category.getName(),
                    category.getCreatedAt()
            );
        }
    }
}
