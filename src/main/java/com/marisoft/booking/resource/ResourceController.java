package com.marisoft.booking.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marisoft.booking.resource.ResourceDto.CreateTextData;
import com.marisoft.booking.resource.ResourceDto.Response;
import com.marisoft.booking.resource.ResourceDto.SimpleResponse;
import com.marisoft.booking.resource.ResourceDto.UpdateTextData;
import com.marisoft.booking.service.ServiceDto;
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
@RequestMapping("/api/resources")
@RequiredArgsConstructor
@Slf4j
public class ResourceController {

    private final ResourceServiceLayer resourceService;
    private final ObjectMapper objectMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Response> getAllResources(
            @RequestParam(required = false) Integer userId
    ) {
        if (userId != null) {
            return resourceService.findByUser(userId).stream()
                    .map(Response::fromEntity)
                    .toList();
        }
        return resourceService.findAll().stream()
                .map(Response::fromEntity)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public Response getResourceById(@PathVariable Integer id) {
        return Response.fromEntity(resourceService.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/services")
    public List<ServiceDto.Response> getServicesByResource(@PathVariable Integer id) {
        return resourceService.getServicesByResource(id).stream()
                .map(ServiceDto.Response::fromEntity)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/by-service/{serviceId}")
    public List<SimpleResponse> getResourcesByService(@PathVariable Integer serviceId) {
        return resourceService.getResourcesByService(serviceId).stream()
                .map(SimpleResponse::fromEntity)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Response createResource(
            @RequestParam("data") String jsonData,
            @RequestParam(value = "image", required = true) MultipartFile image
    ) {
        try {
            CreateTextData request = objectMapper.readValue(jsonData, CreateTextData.class);
            Resource resource = resourceService.create(request, image);
            return Response.fromEntity(resource);
        } catch (Exception e) {
            log.error("Error al procesar la creación: {}", e.getMessage(), e);
            throw new BadRequestException("Error al procesar los datos: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response updateResource(
            @PathVariable Integer id,
            @RequestParam("data") String jsonData,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        try {
            UpdateTextData request = objectMapper.readValue(jsonData, UpdateTextData.class);
            Resource resource = resourceService.update(id, request, image);
            return Response.fromEntity(resource);
        } catch (Exception e) {
            log.error("Error al procesar la actualización: {}", e.getMessage(), e);
            throw new BadRequestException("Error al procesar los datos: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public MessageResponse deleteResource(@PathVariable Integer id) {
        resourceService.delete(id);
        return new MessageResponse("Recurso eliminado exitosamente");
    }
}