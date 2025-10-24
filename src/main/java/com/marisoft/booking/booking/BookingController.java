package com.marisoft.booking.booking;

import com.marisoft.booking.booking.BookingDto.CreateRequest;
import com.marisoft.booking.booking.BookingDto.Response;
import com.marisoft.booking.booking.BookingDto.UpdateRequest;
import com.marisoft.booking.shared.dto.MessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public List<Response> getAllBookings(
            @RequestParam(required = false) Integer customerId,
            @RequestParam(required = false) String status
    ) {
        if (customerId != null) {
            return bookingService.findByCustomer(customerId).stream()
                    .map(Response::fromEntity)
                    .toList();
        }
        if (status != null) {
            return bookingService.findByStatus(status).stream()
                    .map(Response::fromEntity)
                    .toList();
        }
        return bookingService.findAll().stream()
                .map(Response::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public Response getBookingById(@PathVariable Integer id) {
        return Response.fromEntity(bookingService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse createBooking(@Valid @RequestBody CreateRequest request) {
        bookingService.create(request);
        return new MessageResponse("Reserva creada exitosamente");
    }

    @PutMapping("/{id}")
    public MessageResponse updateBooking(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateRequest request
    ) {
        bookingService.update(id, request);
        return new MessageResponse("Reserva actualizada exitosamente");
    }

    @DeleteMapping("/{id}")
    public MessageResponse deleteBooking(@PathVariable Integer id) {
        bookingService.delete(id);
        return new MessageResponse("Reserva eliminada exitosamente");
    }
}