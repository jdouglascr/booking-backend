package com.marisoft.booking.resource;

import com.marisoft.booking.resource.ResourceDto.CreateTextData;
import com.marisoft.booking.resource.ResourceDto.UpdateTextData;
import com.marisoft.booking.service.Service;
import com.marisoft.booking.service.ServiceService;
import com.marisoft.booking.shared.exception.BadRequestException;
import com.marisoft.booking.shared.exception.NotFoundException;
import com.marisoft.booking.shared.images.CloudinaryService;
import com.marisoft.booking.user.User;
import com.marisoft.booking.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Slf4j
public class ResourceServiceLayer {

    private final ResourceRepository resourceRepository;
    private final ResourceServiceRepository resourceServiceRepository;
    private final UserService userService;
    private final ServiceService serviceService;
    private final CloudinaryService cloudinaryService;
    private static final String CLOUDINARY_FOLDER = "resources";

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
        return resourceRepository.findByUserId(userId);
    }

    @Transactional
    public Resource create(CreateTextData request, MultipartFile image) {
        if (resourceRepository.existsByName(request.name())) {
            throw new BadRequestException("Ya existe un recurso con ese nombre");
        }

        if (image == null || image.isEmpty()) {
            throw new BadRequestException("La imagen es obligatoria");
        }

        User user = userService.findById(request.userId());

        List<Service> services = request.serviceIds().stream()
                .map(serviceService::findById)
                .toList();

        String imageUrl = cloudinaryService.uploadImage(image, CLOUDINARY_FOLDER);

        Resource resource = Resource.builder()
                .user(user)
                .name(request.name())
                .resourceType(request.resourceType())
                .description(request.description())
                .imageUrl(imageUrl)
                .build();

        Resource savedResource = resourceRepository.save(resource);

        services.forEach(service -> {
            ResourceService rs = ResourceService.builder()
                    .resource(savedResource)
                    .service(service)
                    .build();
            savedResource.addService(rs);
        });

        log.info("Recurso creado: {} con imagen: {}", savedResource.getName(), imageUrl);
        return resourceRepository.save(savedResource);
    }

    @Transactional
    public Resource update(Integer id, UpdateTextData request, MultipartFile image) {
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

        if (image != null && !image.isEmpty()) {
            String oldImageUrl = resource.getImageUrl();
            String newImageUrl = cloudinaryService.uploadImage(image, CLOUDINARY_FOLDER);
            resource.setImageUrl(newImageUrl);

            if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                cloudinaryService.deleteImage(oldImageUrl);
            }
            log.info("Imagen actualizada para recurso: {} - Nueva: {}", resource.getName(), newImageUrl);
        }

        resource.setUser(user);
        resource.setName(request.name());
        resource.setResourceType(request.resourceType());
        resource.setDescription(request.description());

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

        if (resource.getImageUrl() != null && !resource.getImageUrl().isEmpty()) {
            cloudinaryService.deleteImage(resource.getImageUrl());
            log.info("Imagen eliminada de Cloudinary para recurso: {}", resource.getName());
        }

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