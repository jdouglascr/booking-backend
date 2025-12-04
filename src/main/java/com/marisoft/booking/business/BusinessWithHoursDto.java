package com.marisoft.booking.business;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public sealed interface BusinessWithHoursDto {

    record Response(
            Integer id,
            String name,
            String description,
            String address,
            String phone,
            String email,
            String facebookUrl,
            String instagramUrl,
            String tiktokUrl,
            String logoUrl,
            String bannerUrl,
            List<BusinessHourDto> businessHours,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) implements BusinessWithHoursDto {

        public record BusinessHourDto(
                Integer id,
                String dayOfWeek,
                @JsonFormat(pattern = "HH:mm")
                LocalTime startTime,
                @JsonFormat(pattern = "HH:mm")
                LocalTime endTime,
                boolean isClosed
        ) {
        }
    }

    record UpdateRequest(
            @NotBlank(message = "El nombre es obligatorio")
            @Size(min = 2, max = 255, message = "El nombre debe tener entre 2 y 255 caracteres")
            String name,

            @NotBlank(message = "La descripción es obligatoria")
            String description,

            @NotBlank(message = "La dirección es obligatoria")
            @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
            String address,

            @NotBlank(message = "El teléfono es obligatorio")
            @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
            String phone,

            @NotBlank(message = "El email es obligatorio")
            @Email(message = "Email inválido")
            String email,

            @Size(max = 255, message = "La URL de Facebook no puede exceder 255 caracteres")
            String facebookUrl,

            @Size(max = 255, message = "La URL de Instagram no puede exceder 255 caracteres")
            String instagramUrl,

            @Size(max = 255, message = "La URL de TikTok no puede exceder 255 caracteres")
            String tiktokUrl,

            @NotNull(message = "Los horarios son obligatorios")
            @Valid
            List<BusinessHourUpdateDto> businessHours
    ) implements BusinessWithHoursDto {

        public record BusinessHourUpdateDto(
                @NotNull(message = "El ID del horario es obligatorio")
                Integer id,

                @NotBlank(message = "El día de la semana es obligatorio")
                String dayOfWeek,

                @JsonFormat(pattern = "HH:mm")
                LocalTime startTime,

                @JsonFormat(pattern = "HH:mm")
                LocalTime endTime,

                boolean isClosed
        ) {
        }
    }
}