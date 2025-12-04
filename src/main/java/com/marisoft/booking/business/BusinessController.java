package com.marisoft.booking.business;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marisoft.booking.business.BusinessDto.CreateTextData;
import com.marisoft.booking.business.BusinessWithHoursDto.Response;
import com.marisoft.booking.business.BusinessWithHoursDto.UpdateRequest;
import com.marisoft.booking.shared.dto.MessageResponse;
import com.marisoft.booking.shared.exception.BadRequestException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class BusinessController {

    private final BusinessService businessService;
    private final ObjectMapper objectMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public Response getBusinessWithHours() {
        return businessService.getBusinessWithHours();
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
    public MessageResponse updateBusinessWithHours(
            @RequestParam("data") String jsonData,
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            @RequestParam(value = "banner", required = false) MultipartFile banner
    ) {
        try {
            UpdateRequest request = objectMapper.readValue(jsonData, UpdateRequest.class);
            businessService.updateBusinessWithHours(request, logo, banner);
            return new MessageResponse("Información del negocio y horarios actualizados exitosamente");
        } catch (Exception e) {
            log.error("Error al procesar la actualización: {}", e.getMessage(), e);
            throw new BadRequestException("Error al procesar los datos: " + e.getMessage());
        }
    }
}