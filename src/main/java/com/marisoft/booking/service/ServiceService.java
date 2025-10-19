package com.marisoft.booking.service;

import com.marisoft.booking.category.Category;
import com.marisoft.booking.category.CategoryService;
import com.marisoft.booking.service.ServiceDto.CreateRequest;
import com.marisoft.booking.service.ServiceDto.UpdateRequest;
import com.marisoft.booking.shared.exception.BadRequestException;
import com.marisoft.booking.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final CategoryService categoryService;

    @Transactional(readOnly = true)
    public List<com.marisoft.booking.service.Service> findAll() {
        return serviceRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<com.marisoft.booking.service.Service> findByCategory(Integer categoryId) {
        categoryService.findById(categoryId);
        return serviceRepository.findByCategoryId(categoryId);
    }

    @Transactional(readOnly = true)
    public com.marisoft.booking.service.Service findById(Integer id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Servicio no encontrado"));
    }

    @Transactional
    public void create(CreateRequest request) {
        Category category = categoryService.findById(request.categoryId());

        if (serviceRepository.existsByCategoryIdAndName(request.categoryId(), request.name())) {
            throw new BadRequestException("Ya existe un servicio con ese nombre en esta categoría");
        }

        com.marisoft.booking.service.Service service = com.marisoft.booking.service.Service.builder()
                .category(category)
                .name(request.name())
                .description(request.description())
                .logoUrl(request.logoUrl())
                .durationMin(request.durationMin())
                .bufferTimeMin(request.bufferTimeMin() != null ? request.bufferTimeMin() : 0)
                .price(request.price())
                .build();

        serviceRepository.save(service);
    }

    @Transactional
    public void update(Integer id, UpdateRequest request) {
        com.marisoft.booking.service.Service service = findById(id);
        Category category = categoryService.findById(request.categoryId());

        if (!service.getCategory().getId().equals(request.categoryId()) ||
                !service.getName().equals(request.name())) {
            if (serviceRepository.existsByCategoryIdAndNameAndIdNot(
                    request.categoryId(), request.name(), id)) {
                throw new BadRequestException("Ya existe un servicio con ese nombre en esta categoría");
            }
        }

        service.setCategory(category);
        service.setName(request.name());
        service.setDescription(request.description());
        service.setLogoUrl(request.logoUrl());
        service.setDurationMin(request.durationMin());
        service.setBufferTimeMin(request.bufferTimeMin() != null ? request.bufferTimeMin() : 0);
        service.setPrice(request.price());

        serviceRepository.save(service);
    }

    @Transactional
    public void delete(Integer id) {
        com.marisoft.booking.service.Service service = findById(id);
        serviceRepository.delete(service);
    }
}