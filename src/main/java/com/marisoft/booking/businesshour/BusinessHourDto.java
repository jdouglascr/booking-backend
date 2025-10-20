package com.marisoft.booking.businesshour;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;
import java.time.LocalTime;

public sealed interface BusinessHourDto {

    record CreateRequest(
            @NotBlank(message = "El día de la semana es obligatorio")
            @Pattern(regexp = "Lunes|Martes|Miércoles|Jueves|Viernes|Sábado|Domingo",
                    message = "Día de la semana inválido")
            String dayOfWeek,

            @JsonFormat(pattern = "HH:mm")
            LocalTime startTime,

            @JsonFormat(pattern = "HH:mm")
            LocalTime endTime
    ) implements BusinessHourDto {
    }

    record UpdateRequest(
            @JsonFormat(pattern = "HH:mm")
            LocalTime startTime,

            @JsonFormat(pattern = "HH:mm")
            LocalTime endTime
    ) implements BusinessHourDto {
    }

    record Response(
            Integer id,
            String dayOfWeek,
            @JsonFormat(pattern = "HH:mm")
            LocalTime startTime,
            @JsonFormat(pattern = "HH:mm")
            LocalTime endTime,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) implements BusinessHourDto {
        public static Response fromEntity(BusinessHour businessHour) {
            return new Response(
                    businessHour.getId(),
                    businessHour.getDayOfWeek(),
                    businessHour.getStartTime(),
                    businessHour.getEndTime(),
                    businessHour.getCreatedAt(),
                    businessHour.getUpdatedAt()
            );
        }
    }
}