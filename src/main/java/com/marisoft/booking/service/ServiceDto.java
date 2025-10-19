package com.marisoft.booking.service;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public sealed interface ServiceDto {

    record CreateRequest(
            @NotNull(message = "La categoría es obligatoria")
            Integer categoryId,

            @NotBlank(message = "El nombre es obligatorio")
            @Size(min = 2, max = 255, message = "El nombre debe tener entre 2 y 255 caracteres")
            String name,

            String description,

            @Size(max = 500, message = "La URL del logo no puede exceder 500 caracteres")
            String logoUrl,

            @NotNull(message = "La duración es obligatoria")
            @Min(value = 1, message = "La duración debe ser mayor a 0")
            Integer durationMin,

            @Min(value = 0, message = "El tiempo de buffer no puede ser negativo")
            Integer bufferTimeMin,

            @NotNull(message = "El precio es obligatorio")
            @Min(value = 0, message = "El precio no puede ser negativo")
            Integer price
    ) implements ServiceDto {
    }

    record UpdateRequest(
            @NotNull(message = "La categoría es obligatoria")
            Integer categoryId,

            @NotBlank(message = "El nombre es obligatorio")
            @Size(min = 2, max = 255, message = "El nombre debe tener entre 2 y 255 caracteres")
            String name,

            String description,

            @Size(max = 500, message = "La URL del logo no puede exceder 500 caracteres")
            String logoUrl,

            @NotNull(message = "La duración es obligatoria")
            @Min(value = 1, message = "La duración debe ser mayor a 0")
            Integer durationMin,

            @Min(value = 0, message = "El tiempo de buffer no puede ser negativo")
            Integer bufferTimeMin,

            @NotNull(message = "El precio es obligatorio")
            @Min(value = 0, message = "El precio no puede ser negativo")
            Integer price
    ) implements ServiceDto {
    }

    record Response(
            Integer id,
            Integer categoryId,
            String categoryName,
            String name,
            String description,
            String logoUrl,
            Integer durationMin,
            Integer bufferTimeMin,
            Integer price,
            LocalDateTime createdAt
    ) implements ServiceDto {
        public static Response fromEntity(Service service) {
            return new Response(
                    service.getId(),
                    service.getCategory().getId(),
                    service.getCategory().getName(),
                    service.getName(),
                    service.getDescription(),
                    service.getLogoUrl(),
                    service.getDurationMin(),
                    service.getBufferTimeMin(),
                    service.getPrice(),
                    service.getCreatedAt()
            );
        }
    }
}
