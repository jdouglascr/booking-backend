package com.marisoft.booking.website.dto;

import java.util.List;

public class PublicServiceDto {

    public record Category(
            Integer categoryId,
            String categoryName,
            List<Service> services
    ) {
    }

    public record Service(
            Integer id,
            String name,
            String description,
            String logoUrl,
            Integer durationMin,
            Integer price,
            String priceFormatted,
            String durationFormatted
    ) {
    }
}
