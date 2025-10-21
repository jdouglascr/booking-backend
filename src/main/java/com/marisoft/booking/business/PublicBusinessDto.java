package com.marisoft.booking.business;

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
            String hours;
            boolean isOpen;

            if (businessHour.getStartTime() == null || businessHour.getEndTime() == null) {
                hours = "Cerrado";
                isOpen = false;
            } else {
                String startTime = businessHour.getStartTime().format(TIME_FORMATTER);
                String endTime = businessHour.getEndTime().format(TIME_FORMATTER);
                hours = startTime + " - " + endTime;
                isOpen = true;
            }

            return new BusinessHourDto(
                    businessHour.getDayOfWeek(),
                    hours,
                    isOpen
            );
        }
    }
}