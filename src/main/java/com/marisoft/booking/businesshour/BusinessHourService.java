package com.marisoft.booking.businesshour;

import com.marisoft.booking.businesshour.BusinessHourDto.CreateRequest;
import com.marisoft.booking.businesshour.BusinessHourDto.Response;
import com.marisoft.booking.businesshour.BusinessHourDto.UpdateRequest;
import com.marisoft.booking.shared.exception.BadRequestException;
import com.marisoft.booking.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessHourService {

    private final BusinessHourRepository businessHourRepository;

    @Transactional(readOnly = true)
    public List<Response> getAll() {
        return businessHourRepository.findAll()
                .stream()
                .map(Response::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public Response getById(Integer id) {
        BusinessHour businessHour = businessHourRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Horario no encontrado"));
        return Response.fromEntity(businessHour);
    }

    @Transactional(readOnly = true)
    public Response getByDayOfWeek(String dayOfWeek) {
        BusinessHour businessHour = businessHourRepository.findByDayOfWeek(dayOfWeek)
                .orElseThrow(() -> new NotFoundException("Horario no encontrado para el día: " + dayOfWeek));
        return Response.fromEntity(businessHour);
    }

    @Transactional
    public Response create(CreateRequest request) {
        validateBusinessHours(request.startTime(), request.endTime());

        if (businessHourRepository.existsByDayOfWeek(request.dayOfWeek())) {
            throw new BadRequestException("Ya existe un horario para " + request.dayOfWeek());
        }

        BusinessHour businessHour = BusinessHour.builder()
                .dayOfWeek(request.dayOfWeek())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .build();

        BusinessHour saved = businessHourRepository.save(businessHour);
        return Response.fromEntity(saved);
    }

    @Transactional
    public Response update(Integer id, UpdateRequest request) {
        validateBusinessHours(request.startTime(), request.endTime());

        BusinessHour businessHour = businessHourRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Horario no encontrado"));

        businessHour.setStartTime(request.startTime());
        businessHour.setEndTime(request.endTime());

        BusinessHour updated = businessHourRepository.save(businessHour);
        return Response.fromEntity(updated);
    }

    @Transactional
    public void delete(Integer id) {
        BusinessHour businessHour = businessHourRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Horario no encontrado"));
        businessHourRepository.delete(businessHour);
    }

    private void validateBusinessHours(java.time.LocalTime startTime, java.time.LocalTime endTime) {
        if (startTime == null && endTime == null) {
            return;
        }

        if (startTime == null || endTime == null) {
            throw new BadRequestException("Tanto la hora de inicio como la de fin deben estar presentes o ambas ausentes");
        }

        if (!endTime.isAfter(startTime)) {
            throw new BadRequestException("La hora de fin debe ser posterior a la hora de inicio");
        }
    }
}