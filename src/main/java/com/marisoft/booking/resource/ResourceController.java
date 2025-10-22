package com.marisoft.booking.resource;

import com.marisoft.booking.resource.ResourceDto.CreateRequest;
import com.marisoft.booking.resource.ResourceDto.Response;
import com.marisoft.booking.resource.ResourceDto.SimpleResponse;
import com.marisoft.booking.resource.ResourceDto.UpdateRequest;
import com.marisoft.booking.service.ServiceDto;
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
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceServiceLayer resourceService;

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
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Response createResource(@Valid @RequestBody CreateRequest request) {
        Resource resource = resourceService.create(request);
        return Response.fromEntity(resource);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Response updateResource(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateRequest request
    ) {
        Resource resource = resourceService.update(id, request);
        return Response.fromEntity(resource);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public MessageResponse deleteResource(@PathVariable Integer id) {
        resourceService.delete(id);
        return new MessageResponse("Recurso eliminado exitosamente");
    }
}