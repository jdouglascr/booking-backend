package com.marisoft.booking.business;

import com.marisoft.booking.business.BusinessDto.CreateTextData;
import com.marisoft.booking.business.BusinessDto.Response;
import com.marisoft.booking.business.BusinessDto.UpdateTextData;
import com.marisoft.booking.shared.dto.MessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/business")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public Response getBusiness() {
        return Response.fromEntity(businessService.get());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse createBusiness(
            @Valid @ModelAttribute CreateTextData data,
            @RequestParam(value = "logo", required = true) MultipartFile logo,
            @RequestParam(value = "banner", required = true) MultipartFile banner
    ) {
        businessService.create(data, logo, banner);
        return new MessageResponse("Información del negocio creada exitosamente");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MessageResponse updateBusiness(
            @Valid @ModelAttribute UpdateTextData data,
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            @RequestParam(value = "banner", required = false) MultipartFile banner
    ) {
        businessService.update(data, logo, banner);
        return new MessageResponse("Información del negocio actualizada exitosamente");
    }
}