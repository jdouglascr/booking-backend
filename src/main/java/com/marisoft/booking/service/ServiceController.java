package com.marisoft.booking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marisoft.booking.service.ServiceDto.CreateRequest;
import com.marisoft.booking.service.ServiceDto.Response;
import com.marisoft.booking.service.ServiceDto.UpdateRequest;
import com.marisoft.booking.shared.dto.MessageResponse;
import com.marisoft.booking.shared.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
@Slf4j
public class ServiceController {

    private final ServiceService serviceService;
    private final ObjectMapper objectMapper;

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
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse createService(
            @RequestParam("data") String jsonData,
            @RequestParam(value = "logo", required = false) MultipartFile logo
    ) {
        try {
            CreateRequest request = objectMapper.readValue(jsonData, CreateRequest.class);
            serviceService.create(request, logo);
            return new MessageResponse("Servicio creado exitosamente");
        } catch (Exception e) {
            log.error("Error al procesar la creación: {}", e.getMessage(), e);
            throw new BadRequestException("Error al procesar los datos: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MessageResponse updateService(
            @PathVariable Integer id,
            @RequestParam("data") String jsonData,
            @RequestParam(value = "logo", required = false) MultipartFile logo
    ) {
        try {
            UpdateRequest request = objectMapper.readValue(jsonData, UpdateRequest.class);
            serviceService.update(id, request, logo);
            return new MessageResponse("Servicio actualizado exitosamente");
        } catch (Exception e) {
            log.error("Error al procesar la actualización: {}", e.getMessage(), e);
            throw new BadRequestException("Error al procesar los datos: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public MessageResponse deleteService(@PathVariable Integer id) {
        serviceService.delete(id);
        return new MessageResponse("Servicio eliminado exitosamente");
    }
}