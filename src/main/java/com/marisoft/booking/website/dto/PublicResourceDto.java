package com.marisoft.booking.website.dto;

import com.marisoft.booking.resource.ResourceService;

public record PublicResourceDto(
        Integer resourceServiceId,
        Integer resourceId,
        String name,
        String description,
        String imageUrl
) {
    public static PublicResourceDto fromResourceService(ResourceService rs) {
        return new PublicResourceDto(
                rs.getId(),
                rs.getResource().getId(),
                rs.getResource().getName(),
                rs.getResource().getDescription(),
                rs.getResource().getImageUrl()
        );
    }
}