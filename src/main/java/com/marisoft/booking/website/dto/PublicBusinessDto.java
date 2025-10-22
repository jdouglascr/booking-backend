package com.marisoft.booking.website.dto;

import com.marisoft.booking.businesshour.BusinessHour;

import java.time.format.DateTimeFormatter;
import java.util.List;

public record PublicBusinessDto(
        String name,
        String description,
        String address,
        String phone,
        String email,
        String instagramUrl,
        String tiktokUrl,
        String facebookUrl,
        String logoUrl,
        String bannerUrl,
        List<BusinessHourDto> schedule
) {
    public record BusinessHourDto(
            String day,
            String hours,
            boolean isOpen
    ) {
        private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

        public static BusinessHourDto fromEntity(BusinessHour businessHour) {
            if (businessHour.getStartTime() == null || businessHour.getEndTime() == null) {
                return new BusinessHourDto(businessHour.getDayOfWeek(), "Cerrado", false);
            }

            String startTime = businessHour.getStartTime().format(TIME_FORMATTER);
            String endTime = businessHour.getEndTime().format(TIME_FORMATTER);
            String hours = startTime + " - " + endTime;

            return new BusinessHourDto(businessHour.getDayOfWeek(), hours, true);
        }
    }
}
