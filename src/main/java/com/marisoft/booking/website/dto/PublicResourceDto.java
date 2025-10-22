package com.marisoft.booking.website.dto;

import com.marisoft.booking.resource.Resource;

public record PublicResourceDto(
        Integer id,
        String name,
        String description,
        String imageUrl
) {
    public static PublicResourceDto fromEntity(Resource resource) {
        return new PublicResourceDto(
                resource.getId(),
                resource.getName(),
                resource.getDescription(),
                resource.getImageUrl()
        );
    }
}
