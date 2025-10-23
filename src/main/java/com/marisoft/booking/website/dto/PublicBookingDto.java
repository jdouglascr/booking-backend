package com.marisoft.booking.website.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.marisoft.booking.booking.Booking;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class PublicBookingDto {

    public record CreateRequest(
            @NotNull(message = "El cliente es obligatorio")
            Integer customerId,

            @NotNull(message = "El recurso-servicio es obligatorio")
            Integer resourceServiceId,

            @NotNull(message = "La fecha/hora de inicio es obligatoria")
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            LocalDateTime startDatetime,

            @NotNull(message = "La fecha/hora de fin es obligatoria")
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            LocalDateTime endDatetime,

            @NotNull(message = "El precio es obligatorio")
            @Min(value = 0, message = "El precio no puede ser negativo")
            Integer price,

            String notes
    ) {
    }

    public record Response(
            Integer id,
            Integer customerId,
            String customerName,
            Integer resourceId,
            String resourceName,
            Integer serviceId,
            String serviceName,
            LocalDateTime startDatetime,
            LocalDateTime endDatetime,
            Integer price,
            String status,
            String notes
    ) {
        public static Response fromEntity(Booking booking) {
            return new Response(
                    booking.getId(),
                    booking.getCustomer().getId(),
                    booking.getCustomer().getFirstName() + " " + booking.getCustomer().getLastName(),
                    booking.getResourceService().getResource().getId(),
                    booking.getResourceService().getResource().getName(),
                    booking.getResourceService().getService().getId(),
                    booking.getResourceService().getService().getName(),
                    booking.getStartDatetime(),
                    booking.getEndDatetime(),
                    booking.getPrice(),
                    booking.getStatus(),
                    booking.getNotes()
            );
        }
    }
}