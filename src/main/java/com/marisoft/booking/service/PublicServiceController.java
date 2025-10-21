package com.marisoft.booking.service;

import com.marisoft.booking.service.ServiceDto.PublicServiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/services")
@RequiredArgsConstructor
public class PublicServiceController {

    private final ServiceService serviceService;

    @GetMapping
    public List<PublicServiceResponse> getPublicServices() {
        return serviceService.findAllPublic();
    }
}