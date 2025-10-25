package com.marisoft.booking.business;

import com.marisoft.booking.business.BusinessDto.CreateTextData;
import com.marisoft.booking.business.BusinessDto.UpdateTextData;
import com.marisoft.booking.shared.exception.BadRequestException;
import com.marisoft.booking.shared.exception.NotFoundException;
import com.marisoft.booking.shared.images.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final CloudinaryService cloudinaryService;

    private static final String CLOUDINARY_FOLDER = "business";

    @Transactional(readOnly = true)
    public Business get() {
        return businessRepository.findById(1)
                .orElseThrow(() -> new NotFoundException("Información del negocio no encontrada"));
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
    public void update(UpdateTextData data, MultipartFile logo, MultipartFile banner) {
        Business business = get();

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

            business.setName(data.name());
            business.setDescription(data.description());
            business.setAddress(data.address());
            business.setPhone(data.phone());
            business.setEmail(data.email());
            business.setFacebookUrl(data.facebookUrl());
            business.setInstagramUrl(data.instagramUrl());
            business.setTiktokUrl(data.tiktokUrl());

            businessRepository.save(business);
            log.info("Información del negocio actualizada con éxito");

        } catch (Exception e) {
            log.error("Error al actualizar negocio: {}", e.getMessage(), e);
            throw new BadRequestException("Error al actualizar la información del negocio");
        }
    }
}