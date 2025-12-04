package com.marisoft.booking.service;

import com.marisoft.booking.booking.BookingRepository;
import com.marisoft.booking.category.Category;
import com.marisoft.booking.category.CategoryService;
import com.marisoft.booking.resource.ResourceServiceRepository;
import com.marisoft.booking.service.ServiceDto.CreateRequest;
import com.marisoft.booking.service.ServiceDto.UpdateRequest;
import com.marisoft.booking.shared.enums.BookingStatus;
import com.marisoft.booking.shared.exception.BadRequestException;
import com.marisoft.booking.shared.exception.NotFoundException;
import com.marisoft.booking.shared.images.CloudinaryService;
import com.marisoft.booking.website.dto.PublicServiceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Slf4j
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final CategoryService categoryService;
    private final CloudinaryService cloudinaryService;
    private final BookingRepository bookingRepository;
    private final ResourceServiceRepository resourceServiceRepository;

    private static final String CLOUDINARY_FOLDER = "services";

    @Transactional(readOnly = true)
    public List<Service> findAll() {
        return serviceRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Service> findByCategory(Integer categoryId) {
        categoryService.findById(categoryId);
        return serviceRepository.findByCategoryId(categoryId);
    }

    @Transactional(readOnly = true)
    public Service findById(Integer id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Servicio no encontrado"));
    }

    @Transactional
    public void create(CreateRequest request, MultipartFile logo) {
        Category category = categoryService.findById(request.categoryId());

        if (serviceRepository.existsByCategoryIdAndName(request.categoryId(), request.name())) {
            throw new BadRequestException("Ya existe un servicio con ese nombre en esta categoría");
        }

        String logoUrl = null;
        if (logo != null && !logo.isEmpty()) {
            logoUrl = cloudinaryService.uploadImage(logo, CLOUDINARY_FOLDER);
        }

        Service service = Service.builder()
                .category(category)
                .name(request.name())
                .description(request.description())
                .logoUrl(logoUrl)
                .durationMin(request.durationMin())
                .bufferTimeMin(request.bufferTimeMin() != null ? request.bufferTimeMin() : 0)
                .price(request.price())
                .build();

        serviceRepository.save(service);
        log.info("Servicio creado exitosamente: {}", service.getName());
    }

    @Transactional
    public void update(Integer id, UpdateRequest request, MultipartFile logo) {
        Service service = findById(id);
        Category category = categoryService.findById(request.categoryId());

        if (!service.getCategory().getId().equals(request.categoryId()) ||
                !service.getName().equals(request.name())) {
            if (serviceRepository.existsByCategoryIdAndNameAndIdNot(
                    request.categoryId(), request.name(), id)) {
                throw new BadRequestException("Ya existe un servicio con ese nombre en esta categoría");
            }
        }

        String oldLogoUrl = service.getLogoUrl();

        try {
            if (logo != null && !logo.isEmpty()) {
                String newLogoUrl = cloudinaryService.uploadImage(logo, CLOUDINARY_FOLDER);
                service.setLogoUrl(newLogoUrl);

                if (oldLogoUrl != null && !oldLogoUrl.isEmpty()) {
                    cloudinaryService.deleteImage(oldLogoUrl);
                }
            }

            service.setCategory(category);
            service.setName(request.name());
            service.setDescription(request.description());
            service.setDurationMin(request.durationMin());
            service.setBufferTimeMin(request.bufferTimeMin() != null ? request.bufferTimeMin() : 0);
            service.setPrice(request.price());

            serviceRepository.save(service);
            log.info("Servicio actualizado exitosamente: {}", service.getName());

        } catch (Exception e) {
            log.error("Error al actualizar servicio: {}", e.getMessage(), e);
            throw new BadRequestException("Error al actualizar el servicio: " + e.getMessage());
        }
    }

    @Transactional
    public void delete(Integer id) {
        Service service = findById(id);

        if (bookingRepository.existsFutureBookingsByServiceId(id, LocalDateTime.now(), BookingStatus.CANCELADA)) {
            throw new BadRequestException("No se puede eliminar el servicio porque tiene reservas futuras programadas");
        }

        if (resourceServiceRepository.existsByServiceId(id)) {
            throw new BadRequestException(
                    "No se puede eliminar el servicio porque tiene recursos asignados. Primero desasigne el servicio de todos los recursos"
            );
        }

        if (service.getLogoUrl() != null && !service.getLogoUrl().isEmpty()) {
            cloudinaryService.deleteImage(service.getLogoUrl());
        }

        serviceRepository.delete(service);
        log.info("Servicio eliminado exitosamente: {}", service.getName());
    }

    @Transactional(readOnly = true)
    public List<PublicServiceDto.Category> findAllPublic() {
        List<Service> allServices = serviceRepository.findAll();

        Map<Category, List<Service>> servicesByCategory = allServices.stream()
                .collect(Collectors.groupingBy(Service::getCategory));

        return servicesByCategory.entrySet().stream()
                .map(entry -> new PublicServiceDto.Category(
                        entry.getKey().getId(),
                        entry.getKey().getName(),
                        entry.getValue().stream()
                                .map(service -> new PublicServiceDto.Service(
                                        service.getId(),
                                        service.getName(),
                                        service.getDescription(),
                                        service.getLogoUrl(),
                                        service.getDurationMin(),
                                        service.getPrice(),
                                        formatPrice(service.getPrice()),
                                        formatDuration(service.getDurationMin())
                                ))
                                .toList()
                ))
                .toList();
    }

    private String formatPrice(Integer price) {
        NumberFormat formatter = NumberFormat.getInstance(Locale.of("es", "CL"));
        return "$" + formatter.format(price);
    }

    private String formatDuration(Integer minutes) {
        if (minutes < 60) {
            return minutes + " min";
        }
        int hours = minutes / 60;
        int mins = minutes % 60;
        if (mins == 0) {
            return hours + " h";
        }
        return hours + " h " + mins + " min";
    }
}