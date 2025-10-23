package com.marisoft.booking.website.dto;

import com.marisoft.booking.booking.Booking;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class PublicBookingDto {

    public record CreateRequest(
            @NotNull(message = "El ID del cliente es obligatorio")
            Integer customerId,

            @NotNull(message = "El ID del recurso-servicio es obligatorio")
            Integer resourceServiceId,

            @NotNull(message = "La fecha y hora de inicio es obligatoria")
            LocalDateTime startDatetime,

            @NotNull(message = "La fecha y hora de fin es obligatoria")
            LocalDateTime endDatetime,

            @NotNull(message = "El precio es obligatorio")
            @Min(value = 0, message = "El precio no puede ser negativo")
            Integer price
    ) {
    }

    public record Response(
            Integer id,
            Integer customerId,
            String customerName,
            String resourceName,
            String serviceName,
            LocalDateTime startDatetime,
            LocalDateTime endDatetime,
            Integer price,
            String status,
            String confirmationToken
    ) {
        public static Response fromEntity(Booking booking) {
            return new Response(
                    booking.getId(),
                    booking.getCustomer().getId(),
                    booking.getCustomer().getFirstName() + " " + booking.getCustomer().getLastName(),
                    booking.getResourceService().getResource().getName(),
                    booking.getResourceService().getService().getName(),
                    booking.getStartDatetime(),
                    booking.getEndDatetime(),
                    booking.getPrice(),
                    booking.getStatus().getDisplayName(),
                    booking.getConfirmationToken()
            );
        }
    }
}