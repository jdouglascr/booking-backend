package com.marisoft.booking.businesshour;

import com.marisoft.booking.businesshour.BusinessHourDto.CreateRequest;
import com.marisoft.booking.businesshour.BusinessHourDto.Response;
import com.marisoft.booking.businesshour.BusinessHourDto.UpdateRequest;
import com.marisoft.booking.shared.dto.MessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/business-hours")
@RequiredArgsConstructor
public class BusinessHourController {

    private final BusinessHourService businessHourService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Response> getAllBusinessHours() {
        return businessHourService.getAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public Response getBusinessHourById(@PathVariable Integer id) {
        return businessHourService.getById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/day/{dayOfWeek}")
    public Response getBusinessHourByDay(@PathVariable String dayOfWeek) {
        return businessHourService.getByDayOfWeek(dayOfWeek);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse createBusinessHour(@Valid @RequestBody CreateRequest request) {
        businessHourService.create(request);
        return new MessageResponse("Horario creado exitosamente");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public MessageResponse updateBusinessHour(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateRequest request
    ) {
        businessHourService.update(id, request);
        return new MessageResponse("Horario actualizado exitosamente");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public MessageResponse deleteBusinessHour(@PathVariable Integer id) {
        businessHourService.delete(id);
        return new MessageResponse("Horario eliminado exitosamente");
    }
}