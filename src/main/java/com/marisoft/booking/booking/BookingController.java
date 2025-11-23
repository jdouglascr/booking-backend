package com.marisoft.booking.booking;

import com.marisoft.booking.booking.BookingDto.CreateRequest;
import com.marisoft.booking.booking.BookingDto.Response;
import com.marisoft.booking.booking.BookingDto.UpdateRequest;
import com.marisoft.booking.booking.BookingDto.UpdateStatusRequest;
import com.marisoft.booking.shared.dto.MessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public List<Response> getAllBookings() {
        return bookingService.findAll().stream()
                .map(Response::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public Response getBookingById(@PathVariable Integer id) {
        return Response.fromEntity(bookingService.findById(id));
    }

    @GetMapping("/customer/{customerId}")
    public List<Response> getBookingsByCustomer(@PathVariable Integer customerId) {
        return bookingService.findByCustomer(customerId).stream()
                .map(Response::fromEntity)
                .toList();
    }

    @GetMapping("/status/{status}")
    public List<Response> getBookingsByStatus(@PathVariable String status) {
        return bookingService.findByStatus(status).stream()
                .map(Response::fromEntity)
                .toList();
    }

    @GetMapping("/calendar")
    public List<Response> getBookingsForCalendar(
            @RequestParam Integer resourceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        return bookingService.findByResourceAndDateRange(resourceId, startDate, endDate).stream()
                .map(Response::fromEntity)
                .toList();
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

    @PatchMapping("/{id}/status")
    public MessageResponse updateBookingStatus(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateStatusRequest request
    ) {
        bookingService.updateStatus(id, request);
        return new MessageResponse("Estado actualizado exitosamente");
    }

    @DeleteMapping("/{id}")
    public MessageResponse deleteBooking(@PathVariable Integer id) {
        bookingService.delete(id);
        return new MessageResponse("Reserva eliminada exitosamente");
    }
}