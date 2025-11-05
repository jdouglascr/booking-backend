package com.marisoft.booking.business;

import com.marisoft.booking.business.BusinessDto.CreateTextData;
import com.marisoft.booking.business.BusinessWithHoursDto.Response;
import com.marisoft.booking.business.BusinessWithHoursDto.Response.BusinessHourDto;
import com.marisoft.booking.business.BusinessWithHoursDto.UpdateRequest;
import com.marisoft.booking.businesshour.BusinessHour;
import com.marisoft.booking.businesshour.BusinessHourRepository;
import com.marisoft.booking.shared.exception.BadRequestException;
import com.marisoft.booking.shared.exception.NotFoundException;
import com.marisoft.booking.shared.images.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final BusinessHourRepository businessHourRepository;
    private final CloudinaryService cloudinaryService;

    private static final String CLOUDINARY_FOLDER = "business";

    private static final List<String> DAYS_ORDER = Arrays.asList(
            "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"
    );

    @Transactional(readOnly = true)
    public Business get() {
        return businessRepository.findById(1)
                .orElseThrow(() -> new NotFoundException("Información del negocio no encontrada"));
    }

    @Transactional(readOnly = true)
    public Response getBusinessWithHours() {
        Business business = get();
        List<BusinessHour> businessHours = businessHourRepository.findAll();

        Map<String, BusinessHour> hoursMap = businessHours.stream()
                .collect(Collectors.toMap(BusinessHour::getDayOfWeek, bh -> bh));

        List<BusinessHourDto> orderedHours = DAYS_ORDER.stream()
                .map(day -> {
                    BusinessHour hour = hoursMap.get(day);
                    if (hour == null) {
                        return new BusinessHourDto(null, day, null, null, true);
                    }
                    boolean isClosed = hour.getStartTime() == null || hour.getEndTime() == null;
                    return new BusinessHourDto(
                            hour.getId(),
                            hour.getDayOfWeek(),
                            hour.getStartTime(),
                            hour.getEndTime(),
                            isClosed
                    );
                })
                .toList();

        return new Response(
                business.getId(),
                business.getName(),
                business.getDescription(),
                business.getAddress(),
                business.getPhone(),
                business.getEmail(),
                business.getFacebookUrl(),
                business.getInstagramUrl(),
                business.getTiktokUrl(),
                business.getLogoUrl(),
                business.getBannerUrl(),
                orderedHours,
                business.getCreatedAt(),
                business.getUpdatedAt()
        );
    }

    @Transactional
    public void create(CreateTextData data, MultipartFile logo, MultipartFile banner) {
        if (businessRepository.existsById(1)) {
            throw new BadRequestException("Ya existe información del negocio. Use actualizar en su lugar.");
        }

        String logoUrl = cloudinaryService.uploadImage(logo, CLOUDINARY_FOLDER);
        String bannerUrl = cloudinaryService.uploadImage(banner, CLOUDINARY_FOLDER);

        Business business = Business.builder()
                .name(data.name())
                .description(data.description())
                .address(data.address())
                .phone(data.phone())
                .email(data.email())
                .facebookUrl(data.facebookUrl())
                .instagramUrl(data.instagramUrl())
                .tiktokUrl(data.tiktokUrl())
                .logoUrl(logoUrl)
                .bannerUrl(bannerUrl)
                .build();

        businessRepository.save(business);
        log.info("Información del negocio creada con éxito");
    }

    @Transactional
    public void updateBusinessWithHours(UpdateRequest request, MultipartFile logo, MultipartFile banner) {
        Business business = get();

        validateBusinessHoursUpdate(request.businessHours());

        String oldLogoUrl = business.getLogoUrl();
        String oldBannerUrl = business.getBannerUrl();

        try {
            if (logo != null && !logo.isEmpty()) {
                String newLogoUrl = cloudinaryService.uploadImage(logo, CLOUDINARY_FOLDER);
                business.setLogoUrl(newLogoUrl);
                cloudinaryService.deleteImage(oldLogoUrl);
            }

            if (banner != null && !banner.isEmpty()) {
                String newBannerUrl = cloudinaryService.uploadImage(banner, CLOUDINARY_FOLDER);
                business.setBannerUrl(newBannerUrl);
                cloudinaryService.deleteImage(oldBannerUrl);
            }

            business.setName(request.name());
            business.setDescription(request.description());
            business.setAddress(request.address());
            business.setPhone(request.phone());
            business.setEmail(request.email());
            business.setFacebookUrl(request.facebookUrl());
            business.setInstagramUrl(request.instagramUrl());
            business.setTiktokUrl(request.tiktokUrl());

            businessRepository.save(business);

            updateBusinessHours(request.businessHours());

            log.info("Información del negocio y horarios actualizados con éxito");

        } catch (Exception e) {
            log.error("Error al actualizar negocio y horarios: {}", e.getMessage(), e);
            throw new BadRequestException("Error al actualizar la información: " + e.getMessage());
        }
    }

    private void validateBusinessHoursUpdate(List<UpdateRequest.BusinessHourUpdateDto> hours) {
        if (hours == null || hours.isEmpty()) {
            throw new BadRequestException("Debe proporcionar horarios para todos los días");
        }

        List<String> providedDays = hours.stream()
                .map(UpdateRequest.BusinessHourUpdateDto::dayOfWeek)
                .toList();

        List<String> missingDays = DAYS_ORDER.stream()
                .filter(day -> !providedDays.contains(day))
                .toList();

        if (!missingDays.isEmpty()) {
            throw new BadRequestException("Faltan horarios para los siguientes días: " +
                    String.join(", ", missingDays));
        }

        for (UpdateRequest.BusinessHourUpdateDto hour : hours) {
            if (!hour.isClosed()) {
                validateBusinessHourTimes(hour.startTime(), hour.endTime(), hour.dayOfWeek());
            }
        }
    }

    private void validateBusinessHourTimes(LocalTime startTime, LocalTime endTime, String dayOfWeek) {
        if (startTime == null || endTime == null) {
            throw new BadRequestException(
                    "Las horas de inicio y fin son obligatorias para " + dayOfWeek +
                            " si no está marcado como cerrado"
            );
        }

        if (!endTime.isAfter(startTime)) {
            throw new BadRequestException(
                    "La hora de fin debe ser posterior a la hora de inicio para " + dayOfWeek
            );
        }
    }

    private void updateBusinessHours(List<UpdateRequest.BusinessHourUpdateDto> hours) {
        for (UpdateRequest.BusinessHourUpdateDto hourDto : hours) {
            BusinessHour businessHour = businessHourRepository.findById(hourDto.id())
                    .orElseThrow(() -> new NotFoundException(
                            "Horario no encontrado para " + hourDto.dayOfWeek()
                    ));

            if (hourDto.isClosed()) {
                businessHour.setStartTime(null);
                businessHour.setEndTime(null);
            } else {
                businessHour.setStartTime(hourDto.startTime());
                businessHour.setEndTime(hourDto.endTime());
            }

            businessHourRepository.save(businessHour);
        }
    }
}