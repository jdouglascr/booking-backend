package com.marisoft.booking.resource;

import com.marisoft.booking.resource.ResourceDto.CreateRequest;
import com.marisoft.booking.resource.ResourceDto.UpdateRequest;
import com.marisoft.booking.service.Service;
import com.marisoft.booking.service.ServiceService;
import com.marisoft.booking.shared.exception.BadRequestException;
import com.marisoft.booking.shared.exception.NotFoundException;
import com.marisoft.booking.user.User;
import com.marisoft.booking.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ResourceServiceLayer {

    private final ResourceRepository resourceRepository;
    private final ResourceServiceRepository resourceServiceRepository;
    private final UserService userService;
    private final ServiceService serviceService;

    @Transactional(readOnly = true)
    public List<Resource> findAll() {
        return resourceRepository.findAllWithServices();
    }

    @Transactional(readOnly = true)
    public Resource findById(Integer id) {
        return resourceRepository.findByIdWithServices(id)
                .orElseThrow(() -> new NotFoundException("Recurso no encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Resource> findByUser(Integer userId) {
        userService.findById(userId);
        return resourceRepository.findByUserId(userId);
    }

    @Transactional
    public Resource create(CreateRequest request) {
        if (resourceRepository.existsByName(request.name())) {
            throw new BadRequestException("Ya existe un recurso con ese nombre");
        }

        User user = userService.findById(request.userId());

        List<Service> services = request.serviceIds().stream()
                .map(serviceService::findById)
                .toList();

        Resource resource = Resource.builder()
                .user(user)
                .name(request.name())
                .resourceType(request.resourceType())
                .description(request.description())
                .imageUrl(request.imageUrl())
                .build();

        Resource savedResource = resourceRepository.save(resource);

        services.forEach(service -> {
            ResourceService rs = ResourceService.builder()
                    .resource(savedResource)
                    .service(service)
                    .build();
            savedResource.addService(rs);
        });

        return resourceRepository.save(savedResource);
    }

    @Transactional
    public Resource update(Integer id, UpdateRequest request) {
        Resource resource = findById(id);

        if (!resource.getName().equals(request.name())) {
            if (resourceRepository.existsByNameAndIdNot(request.name(), id)) {
                throw new BadRequestException("Ya existe un recurso con ese nombre");
            }
        }

        User user = userService.findById(request.userId());

        List<Service> services = request.serviceIds().stream()
                .map(serviceService::findById)
                .toList();

        resource.setUser(user);
        resource.setName(request.name());
        resource.setResourceType(request.resourceType());
        resource.setDescription(request.description());
        resource.setImageUrl(request.imageUrl());

        resource.getResourceServices().removeIf(rs ->
                !request.serviceIds().contains(rs.getService().getId())
        );

        List<Integer> existingServiceIds = resource.getResourceServices().stream()
                .map(rs -> rs.getService().getId())
                .toList();

        services.stream()
                .filter(service -> !existingServiceIds.contains(service.getId()))
                .forEach(service -> {
                    ResourceService rs = ResourceService.builder()
                            .resource(resource)
                            .service(service)
                            .build();
                    resource.addService(rs);
                });

        return resourceRepository.save(resource);
    }

    @Transactional
    public void delete(Integer id) {
        Resource resource = findById(id);
        resourceRepository.delete(resource);
    }

    @Transactional(readOnly = true)
    public List<Service> getServicesByResource(Integer resourceId) {
        Resource resource = findById(resourceId);
        return resource.getResourceServices().stream()
                .map(ResourceService::getService)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ResourceService> getResourceServicesByService(Integer serviceId) {
        serviceService.findById(serviceId);
        return resourceServiceRepository.findByServiceId(serviceId);
    }

    @Transactional(readOnly = true)
    public List<Resource> getResourcesByService(Integer serviceId) {
        serviceService.findById(serviceId);
        return resourceServiceRepository.findByServiceId(serviceId).stream()
                .map(ResourceService::getResource)
                .toList();
    }
}