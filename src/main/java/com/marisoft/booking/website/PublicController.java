package com.marisoft.booking.website;

import com.marisoft.booking.business.Business;
import com.marisoft.booking.business.BusinessService;
import com.marisoft.booking.businesshour.BusinessHourRepository;
import com.marisoft.booking.resource.ResourceServiceLayer;
import com.marisoft.booking.service.ServiceService;
import com.marisoft.booking.website.dto.PublicBusinessDto;
import com.marisoft.booking.website.dto.PublicResourceDto;
import com.marisoft.booking.website.dto.PublicServiceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final BusinessService businessService;
    private final BusinessHourRepository businessHourRepository;
    private final ServiceService serviceService;
    private final ResourceServiceLayer resourceService;

    @GetMapping("/business")
    public PublicBusinessDto getBusinessInfo() {
        Business business = businessService.get();

        List<String> daysOrder = Arrays.asList(
                "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"
        );

        List<PublicBusinessDto.BusinessHourDto> schedule = daysOrder.stream()
                .map(day -> businessHourRepository.findByDayOfWeek(day)
                        .map(PublicBusinessDto.BusinessHourDto::fromEntity)
                        .orElse(new PublicBusinessDto.BusinessHourDto(day, "Cerrado", false))
                )
                .toList();

        return new PublicBusinessDto(
                business.getName(),
                business.getDescription(),
                business.getAddress(),
                business.getPhone(),
                business.getEmail(),
                business.getInstagramUrl(),
                business.getTiktokUrl(),
                business.getFacebookUrl(),
                business.getLogoUrl(),
                business.getBannerUrl(),
                schedule
        );
    }

    @GetMapping("/services")
    public List<PublicServiceDto.Category> getPublicServices() {
        return serviceService.findAllPublic();
    }

    @GetMapping("/resources/by-service/{serviceId}")
    public List<PublicResourceDto> getResourcesByService(@PathVariable Integer serviceId) {
        return resourceService.getResourcesByService(serviceId).stream()
                .map(PublicResourceDto::fromEntity)
                .toList();
    }
}