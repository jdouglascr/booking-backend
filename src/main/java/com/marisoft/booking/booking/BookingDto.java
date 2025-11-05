package com.marisoft.booking.booking;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public sealed interface BookingDto {

    record CreateRequest(
            @NotNull(message = "El cliente es obligatorio")
            Integer customerId,

            @NotNull(message = "El recurso-servicio es obligatorio")
            Integer resourceServiceId,

            @NotNull(message = "La fecha/hora de inicio es obligatoria")
            LocalDateTime startDatetime,

            @NotNull(message = "La fecha/hora de fin es obligatoria")
            LocalDateTime endDatetime,

            @NotNull(message = "El precio es obligatorio")
            @Min(value = 0, message = "El precio no puede ser negativo")
            Integer price,

            @Size(max = 100, message = "El token de confirmación no puede exceder 100 caracteres")
            String confirmationToken
    ) implements BookingDto {
    }

    record UpdateRequest(
            @NotNull(message = "El cliente es obligatorio")
            Integer customerId,

            @NotNull(message = "El recurso-servicio es obligatorio")
            Integer resourceServiceId,

            @NotNull(message = "La fecha/hora de inicio es obligatoria")
            LocalDateTime startDatetime,

            @NotNull(message = "La fecha/hora de fin es obligatoria")
            LocalDateTime endDatetime,

            @NotNull(message = "El precio es obligatorio")
            @Min(value = 0, message = "El precio no puede ser negativo")
            Integer price,

            @NotBlank(message = "El estado es obligatorio")
            String status,

            String cancellationReason,

            @Size(max = 100, message = "El campo cancelledBy no puede exceder 100 caracteres")
            String cancelledBy,

            LocalDateTime cancelledAt,

            @Size(max = 100, message = "El token de confirmación no puede exceder 100 caracteres")
            String confirmationToken
    ) implements BookingDto {
    }

    record Response(
            Integer id,
            Integer customerId,
            String customerName,
            String customerEmail,
            String customerPhone,
            Integer resourceServiceId,
            Integer resourceId,
            String resourceName,
            Integer serviceId,
            String serviceName,
            LocalDateTime startDatetime,
            LocalDateTime endDatetime,
            Integer price,
            String status,
            String cancellationReason,
            String cancelledBy,
            LocalDateTime cancelledAt,
            String confirmationToken,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) implements BookingDto {
        public static Response fromEntity(Booking booking) {
            return new Response(
                    booking.getId(),
                    booking.getCustomer().getId(),
                    booking.getCustomer().getFirstName() + " " + booking.getCustomer().getLastName(),
                    booking.getCustomer().getEmail(),
                    booking.getCustomer().getPhone(),
                    booking.getResourceService().getId(),
                    booking.getResourceService().getResource().getId(),
                    booking.getResourceService().getResource().getName(),
                    booking.getResourceService().getService().getId(),
                    booking.getResourceService().getService().getName(),
                    booking.getStartDatetime(),
                    booking.getEndDatetime(),
                    booking.getPrice(),
                    booking.getStatus(),
                    booking.getCancellationReason(),
                    booking.getCancelledBy(),
                    booking.getCancelledAt(),
                    booking.getConfirmationToken(),
                    booking.getCreatedAt(),
                    booking.getUpdatedAt()
            );
        }
    }
}