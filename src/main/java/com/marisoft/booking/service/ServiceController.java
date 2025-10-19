package com.marisoft.booking.service;

import com.marisoft.booking.service.ServiceDto.CreateRequest;
import com.marisoft.booking.service.ServiceDto.Response;
import com.marisoft.booking.service.ServiceDto.UpdateRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Response> getAllServices(
            @RequestParam(required = false) Integer categoryId
    ) {
        if (categoryId != null) {
            return serviceService.findByCategory(categoryId).stream()
                    .map(Response::fromEntity)
                    .toList();
        }
        return serviceService.findAll().stream()
                .map(Response::fromEntity)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public Response getServiceById(@PathVariable Integer id) {
        return Response.fromEntity(serviceService.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse createService(@Valid @RequestBody CreateRequest request) {
        serviceService.create(request);
        return new MessageResponse("Servicio creado exitosamente");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public MessageResponse updateService(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateRequest request
    ) {
        serviceService.update(id, request);
        return new MessageResponse("Servicio actualizado exitosamente");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public MessageResponse deleteService(@PathVariable Integer id) {
        serviceService.delete(id);
        return new MessageResponse("Servicio eliminado exitosamente");
    }
}