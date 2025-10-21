package com.marisoft.booking.business;

import com.marisoft.booking.businesshour.BusinessHourRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/public/business")
@RequiredArgsConstructor
public class PublicBusinessController {

    private final BusinessService businessService;
    private final BusinessHourRepository businessHourRepository;

    @GetMapping
    public PublicBusinessDto getPublicBusinessInfo() {
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
}