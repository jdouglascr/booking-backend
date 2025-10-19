package com.marisoft.booking.business;

import com.marisoft.booking.business.BusinessDto.CreateRequest;
import com.marisoft.booking.business.BusinessDto.Response;
import com.marisoft.booking.business.BusinessDto.UpdateRequest;
import com.marisoft.booking.shared.dto.MessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse createBusiness(@Valid @RequestBody CreateRequest request) {
        businessService.create(request);
        return new MessageResponse("Información del negocio creada exitosamente");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    public MessageResponse updateBusiness(@Valid @RequestBody UpdateRequest request) {
        businessService.update(request);
        return new MessageResponse("Información del negocio actualizada exitosamente");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping
    public MessageResponse deleteBusiness() {
        businessService.delete();
        return new MessageResponse("Información del negocio eliminada exitosamente");
    }
}
