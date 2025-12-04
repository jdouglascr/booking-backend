package com.marisoft.booking.resource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public sealed interface ResourceDto {

    record CreateTextData(
            @NotNull(message = "El usuario responsable es obligatorio")
            Integer userId,

            @NotBlank(message = "El nombre es obligatorio")
            @Size(min = 2, max = 255, message = "El nombre debe tener entre 2 y 255 caracteres")
            String name,

            @NotNull(message = "El tipo de recurso es obligatorio")
            @Pattern(regexp = "Profesional|Infraestructura", message = "El tipo debe ser 'Profesional' o 'Infraestructura'")
            String resourceType,

            String description,

            @NotEmpty(message = "Debe asignar al menos un servicio")
            List<Integer> serviceIds
    ) implements ResourceDto {
    }

    record UpdateTextData(
            @NotNull(message = "El usuario responsable es obligatorio")
            Integer userId,

            @NotBlank(message = "El nombre es obligatorio")
            @Size(min = 2, max = 255, message = "El nombre debe tener entre 2 y 255 caracteres")
            String name,

            @NotNull(message = "El tipo de recurso es obligatorio")
            @Pattern(regexp = "Profesional|Infraestructura", message = "El tipo debe ser 'Profesional' o 'Infraestructura'")
            String resourceType,

            String description,

            @NotEmpty(message = "Debe asignar al menos un servicio")
            List<Integer> serviceIds
    ) implements ResourceDto {
    }

    record Response(
            Integer id,
            Integer userId,
            String userName,
            String name,
            String resourceType,
            String description,
            String imageUrl,
            List<ServiceInfo> services,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) implements ResourceDto {
        public static Response fromEntity(Resource resource) {
            List<ServiceInfo> services = resource.getResourceServices().stream()
                    .map(rs -> new ServiceInfo(
                            rs.getService().getId(),
                            rs.getService().getName(),
                            rs.getService().getCategory().getName(),
                            rs.getService().getDurationMin(),
                            rs.getService().getPrice()
                    ))
                    .toList();

            return new Response(
                    resource.getId(),
                    resource.getUser().getId(),
                    resource.getUser().getFullName(),
                    resource.getName(),
                    resource.getResourceType(),
                    resource.getDescription(),
                    resource.getImageUrl(),
                    services,
                    resource.getCreatedAt(),
                    resource.getUpdatedAt()
            );
        }
    }

    record ServiceInfo(
            Integer id,
            String name,
            String categoryName,
            Integer durationMin,
            Integer price
    ) implements ResourceDto {
    }

    record SimpleResponse(
            Integer id,
            String name,
            String resourceType,
            String userName,
            String imageUrl
    ) implements ResourceDto {
        public static SimpleResponse fromEntity(Resource resource) {
            return new SimpleResponse(
                    resource.getId(),
                    resource.getName(),
                    resource.getResourceType(),
                    resource.getUser().getFullName(),
                    resource.getImageUrl()
            );
        }
    }
}