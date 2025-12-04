package com.marisoft.booking.website.dto;

import java.util.List;

public class PublicAvailabilityDto {

    public record WeekAvailabilityResponse(
            Integer resourceServiceId,
            String weekStart,
            String weekEnd,
            String weekHeader,
            NavigationDto navigation,
            List<DayScheduleDto> weekSchedule
    ) {
    }

    public record NavigationDto(
            boolean canGoPrevious,
            boolean canGoNext
    ) {
    }

    public record DayScheduleDto(
            String date,
            String dayOfWeek,
            String shortName,
            Integer dayNumber,
            boolean isAvailable,
            List<String> timeSlots
    ) {
    }
}