package com.marisoft.booking.resource;

import com.marisoft.booking.resource.ResourceDto.PublicResourceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/resources")
@RequiredArgsConstructor
public class PublicResourceController {

    private final ResourceServiceLayer resourceService;

    @GetMapping("/service/{serviceId}")
    public List<PublicResourceResponse> getResourcesByService(@PathVariable Integer serviceId) {
        return resourceService.getResourcesByService(serviceId).stream()
                .map(PublicResourceResponse::fromEntity)
                .toList();
    }
}