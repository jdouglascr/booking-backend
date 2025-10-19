package com.marisoft.booking.business;

import com.marisoft.booking.business.BusinessDto.CreateRequest;
import com.marisoft.booking.business.BusinessDto.UpdateRequest;
import com.marisoft.booking.shared.exception.BadRequestException;
import com.marisoft.booking.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BusinessService {

    private final BusinessRepository businessRepository;

    @Transactional(readOnly = true)
    public Business get() {
        return businessRepository.findById(1)
                .orElseThrow(() -> new NotFoundException("Información del negocio no encontrada"));
    }

    @Transactional
    public void create(CreateRequest request) {
        if (businessRepository.existsById(1)) {
            throw new BadRequestException("Ya existe información del negocio. Use actualizar en su lugar.");
        }

        Business business = Business.builder()
                .name(request.name())
                .description(request.description())
                .address(request.address())
                .phone(request.phone())
                .email(request.email())
                .facebookUrl(request.facebookUrl())
                .instagramUrl(request.instagramUrl())
                .tiktokUrl(request.tiktokUrl())
                .logoUrl(request.logoUrl())
                .bannerUrl(request.bannerUrl())
                .build();

        businessRepository.save(business);
    }

    @Transactional
    public void update(UpdateRequest request) {
        Business business = get();

        business.setName(request.name());
        business.setDescription(request.description());
        business.setAddress(request.address());
        business.setPhone(request.phone());
        business.setEmail(request.email());
        business.setFacebookUrl(request.facebookUrl());
        business.setInstagramUrl(request.instagramUrl());
        business.setTiktokUrl(request.tiktokUrl());
        business.setLogoUrl(request.logoUrl());
        business.setBannerUrl(request.bannerUrl());

        businessRepository.save(business);
    }

    @Transactional
    public void delete() {
        Business business = get();
        businessRepository.delete(business);
    }
}
