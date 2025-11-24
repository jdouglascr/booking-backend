package com.marisoft.booking.availability;

import com.marisoft.booking.booking.Booking;
import com.marisoft.booking.booking.BookingRepository;
import com.marisoft.booking.businesshour.BusinessHour;
import com.marisoft.booking.businesshour.BusinessHourRepository;
import com.marisoft.booking.resource.ResourceService;
import com.marisoft.booking.resource.ResourceServiceRepository;
import com.marisoft.booking.shared.enums.BookingStatus;
import com.marisoft.booking.shared.exception.NotFoundException;
import com.marisoft.booking.website.dto.PublicAvailabilityDto.DayScheduleDto;
import com.marisoft.booking.website.dto.PublicAvailabilityDto.NavigationDto;
import com.marisoft.booking.website.dto.PublicAvailabilityDto.WeekAvailabilityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final ResourceServiceRepository resourceServiceRepository;
    private final BusinessHourRepository businessHourRepository;
    private final BookingRepository bookingRepository;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final Locale SPANISH_LOCALE = Locale.of("es", "CL");

    @Transactional(readOnly = true)
    public WeekAvailabilityResponse getWeekAvailability(Integer resourceServiceId, LocalDate startDate) {
        // Validar que el resourceService existe y obtener duración del servicio
        ResourceService resourceService = resourceServiceRepository.findById(resourceServiceId)
                .orElseThrow(() -> new NotFoundException("Recurso-Servicio no encontrado"));

        Integer serviceDurationMin = resourceService.getService().getDurationMin();
        Integer resourceId = resourceService.getResource().getId();

        // Calcular rango de la semana
        LocalDate weekStart = startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);

        // Obtener horarios de negocio
        List<BusinessHour> businessHours = businessHourRepository.findAll();
        Map<String, BusinessHour> businessHoursMap = businessHours.stream()
                .collect(Collectors.toMap(BusinessHour::getDayOfWeek, bh -> bh));

        // Obtener todas las reservas del recurso en la semana
        LocalDateTime weekStartDateTime = weekStart.atStartOfDay();
        LocalDateTime weekEndDateTime = weekEnd.atTime(23, 59, 59);

        List<Booking> existingBookings = bookingRepository.findByResourceAndDateRange(
                        resourceId, weekStartDateTime, weekEndDateTime
                ).stream()
                .filter(b -> b.getStatus() != BookingStatus.CANCELADA &&
                        b.getStatus() != BookingStatus.COMPLETADA)
                .toList();

        // Generar disponibilidad por día
        List<DayScheduleDto> weekSchedule = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = weekStart.plusDays(i);
            DayScheduleDto daySchedule = generateDaySchedule(
                    currentDate,
                    businessHoursMap,
                    existingBookings,
                    serviceDurationMin
            );
            weekSchedule.add(daySchedule);
        }

        // Construir respuesta
        return new WeekAvailabilityResponse(
                resourceServiceId,
                weekStart.toString(),
                weekEnd.toString(),
                formatWeekHeader(weekStart, weekEnd),
                new NavigationDto(canGoPrevious(weekStart), true),
                weekSchedule
        );
    }

    private DayScheduleDto generateDaySchedule(
            LocalDate date,
            Map<String, BusinessHour> businessHoursMap,
            List<Booking> existingBookings,
            Integer serviceDurationMin
    ) {
        String dayOfWeekSpanish = getDayOfWeekSpanish(date.getDayOfWeek());
        BusinessHour businessHour = businessHoursMap.get(dayOfWeekSpanish);

        // Si no hay horario o está cerrado
        if (businessHour == null || businessHour.getStartTime() == null || businessHour.getEndTime() == null) {
            return new DayScheduleDto(
                    date.toString(),
                    dayOfWeekSpanish,
                    getShortDayName(dayOfWeekSpanish),
                    date.getDayOfWeek().getValue() % 7,
                    false,
                    List.of()
            );
        }

        // Generar bloques disponibles
        List<String> availableSlots = generateAvailableSlots(
                date,
                businessHour.getStartTime(),
                businessHour.getEndTime(),
                serviceDurationMin,
                existingBookings
        );

        return new DayScheduleDto(
                date.toString(),
                dayOfWeekSpanish,
                getShortDayName(dayOfWeekSpanish),
                date.getDayOfWeek().getValue() % 7,
                !availableSlots.isEmpty(),
                availableSlots
        );
    }

    private List<String> generateAvailableSlots(
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            Integer serviceDurationMin,
            List<Booking> existingBookings
    ) {
        List<String> slots = new ArrayList<>();
        LocalTime currentTime = startTime;
        LocalDateTime now = LocalDateTime.now();

        // Filtrar bookings del día actual
        List<Booking> dayBookings = existingBookings.stream()
                .filter(b -> b.getStartDatetime().toLocalDate().equals(date))
                .toList();

        while (currentTime.plusMinutes(serviceDurationMin).isBefore(endTime) ||
                currentTime.plusMinutes(serviceDurationMin).equals(endTime)) {

            LocalDateTime slotStart = date.atTime(currentTime);
            LocalDateTime slotEnd = slotStart.plusMinutes(serviceDurationMin);

            // Validar que no sea en el pasado
            if (slotStart.isBefore(now)) {
                currentTime = currentTime.plusMinutes(serviceDurationMin);
                continue;
            }

            // Verificar si hay conflicto con bookings existentes
            boolean hasConflict = dayBookings.stream()
                    .anyMatch(booking -> {
                        Integer bufferTime = booking.getResourceService().getService().getBufferTimeMin();
                        LocalDateTime bookingEndWithBuffer = booking.getEndDatetime().plusMinutes(bufferTime);

                        return isOverlapping(slotStart, slotEnd, booking.getStartDatetime(), bookingEndWithBuffer);
                    });

            if (!hasConflict) {
                slots.add(currentTime.format(TIME_FORMATTER));
            }

            currentTime = currentTime.plusMinutes(serviceDurationMin);
        }

        return slots;
    }

    private boolean isOverlapping(LocalDateTime slotStart, LocalDateTime slotEnd,
                                  LocalDateTime bookingStart, LocalDateTime bookingEndWithBuffer) {
        // Dos intervalos se solapan
        return slotStart.isBefore(bookingEndWithBuffer) &&
                bookingStart.isBefore(slotEnd);
    }

    private boolean canGoPrevious(LocalDate weekStart) {
        LocalDate today = LocalDate.now();
        return weekStart.isAfter(today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
    }

    private String formatWeekHeader(LocalDate start, LocalDate end) {
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("d", SPANISH_LOCALE);
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", SPANISH_LOCALE);

        String startDay = start.format(dayFormatter);
        String endDay = end.format(dayFormatter);
        String month = end.format(monthFormatter);

        return startDay + " - " + endDay + ", " + capitalize(month);
    }

    private String getDayOfWeekSpanish(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "Lunes";
            case TUESDAY -> "Martes";
            case WEDNESDAY -> "Miércoles";
            case THURSDAY -> "Jueves";
            case FRIDAY -> "Viernes";
            case SATURDAY -> "Sábado";
            case SUNDAY -> "Domingo";
        };
    }

    private String getShortDayName(String fullDayName) {
        return switch (fullDayName) {
            case "Lunes" -> "Lu";
            case "Martes" -> "Ma";
            case "Miércoles" -> "Mi";
            case "Jueves" -> "Ju";
            case "Viernes" -> "Vi";
            case "Sábado" -> "Sa";
            case "Domingo" -> "Do";
            default -> fullDayName.substring(0, 2);
        };
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}