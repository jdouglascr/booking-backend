package com.marisoft.booking.business;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public sealed interface BusinessDto {

    record CreateRequest(
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

            @NotBlank(message = "La URL del logo es obligatoria")
            @Size(max = 255, message = "La URL del logo no puede exceder 255 caracteres")
            String logoUrl,

            @NotBlank(message = "La URL del banner es obligatoria")
            @Size(max = 255, message = "La URL del banner no puede exceder 255 caracteres")
            String bannerUrl
    ) implements BusinessDto {
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

            @NotBlank(message = "La URL del logo es obligatoria")
            @Size(max = 255, message = "La URL del logo no puede exceder 255 caracteres")
            String logoUrl,

            @NotBlank(message = "La URL del banner es obligatoria")
            @Size(max = 255, message = "La URL del banner no puede exceder 255 caracteres")
            String bannerUrl
    ) implements BusinessDto {
    }

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
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) implements BusinessDto {
        public static Response fromEntity(Business business) {
            return new Response(
                    business.getId(),
                    business.getName(),
                    business.getDescription(),
                    business.getAddress(),
                    business.getPhone(),
                    business.getEmail(),
                    business.getFacebookUrl(),
                    business.getInstagramUrl(),
                    business.getTiktokUrl(),
                    business.getLogoUrl(),
                    business.getBannerUrl(),
                    business.getCreatedAt(),
                    business.getUpdatedAt()
            );
        }
    }
}
