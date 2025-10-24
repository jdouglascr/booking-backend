package com.marisoft.booking.website;

import com.marisoft.booking.booking.Booking;
import com.marisoft.booking.booking.BookingService;
import com.marisoft.booking.business.Business;
import com.marisoft.booking.business.BusinessService;
import com.marisoft.booking.businesshour.BusinessHourRepository;
import com.marisoft.booking.customer.Customer;
import com.marisoft.booking.customer.CustomerService;
import com.marisoft.booking.resource.ResourceServiceLayer;
import com.marisoft.booking.service.ServiceService;
import com.marisoft.booking.shared.dto.MessageResponse;
import com.marisoft.booking.website.dto.PublicBookingDto;
import com.marisoft.booking.website.dto.PublicBusinessDto;
import com.marisoft.booking.website.dto.PublicCustomerDto;
import com.marisoft.booking.website.dto.PublicResourceDto;
import com.marisoft.booking.website.dto.PublicServiceDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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
    private final CustomerService customerService;
    private final BookingService bookingService;

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
        return resourceService.getResourceServicesByService(serviceId).stream()
                .map(PublicResourceDto::fromResourceService)
                .toList();
    }

    @PostMapping("/customers")
    @ResponseStatus(HttpStatus.OK)
    public PublicCustomerDto.Response upsertCustomer(
            @Valid @RequestBody PublicCustomerDto.UpsertRequest request
    ) {
        Customer customer = customerService.upsert(request);
        return PublicCustomerDto.Response.fromEntity(customer);
    }

    @PostMapping("/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    public PublicBookingDto.Response createBooking(
            @Valid @RequestBody PublicBookingDto.CreateRequest request
    ) {
        Booking booking = bookingService.createPublic(request);
        return PublicBookingDto.Response.fromEntity(booking);
    }

    @PatchMapping("/bookings/{token}/confirm")
    public MessageResponse confirmBooking(@PathVariable String token) {
        bookingService.confirmBooking(token);
        return new MessageResponse("Reserva confirmada exitosamente");
    }

    @PatchMapping("/bookings/{token}/cancel")
    public MessageResponse cancelBooking(
            @PathVariable String token,
            @RequestBody(required = false) CancelRequest request
    ) {
        String reason = request != null ? request.reason() : null;
        bookingService.cancelBooking(token, reason);
        return new MessageResponse("Reserva cancelada exitosamente");
    }

    public record CancelRequest(String reason) {
    }
}