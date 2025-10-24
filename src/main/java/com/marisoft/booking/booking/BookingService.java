package com.marisoft.booking.booking;

import com.marisoft.booking.booking.BookingDto.CreateRequest;
import com.marisoft.booking.booking.BookingDto.UpdateRequest;
import com.marisoft.booking.customer.Customer;
import com.marisoft.booking.customer.CustomerService;
import com.marisoft.booking.resource.ResourceService;
import com.marisoft.booking.resource.ResourceServiceRepository;
import com.marisoft.booking.shared.email.BookingEmailDto;
import com.marisoft.booking.shared.email.EmailService;
import com.marisoft.booking.shared.exception.BadRequestException;
import com.marisoft.booking.shared.exception.NotFoundException;
import com.marisoft.booking.website.dto.PublicBookingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CustomerService customerService;
    private final ResourceServiceRepository resourceServiceRepository;
    private final EmailService emailService;

    @Transactional(readOnly = true)
    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Booking findById(Integer id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reserva no encontrada"));
    }

    @Transactional(readOnly = true)
    public Booking findByConfirmationToken(String token) {
        return bookingRepository.findByConfirmationToken(token)
                .orElseThrow(() -> new NotFoundException("Reserva no encontrada"));
    }

    @Transactional(readOnly = true)
    public List<Booking> findByCustomer(Integer customerId) {
        customerService.findById(customerId);
        return bookingRepository.findByCustomerId(customerId);
    }

    @Transactional(readOnly = true)
    public List<Booking> findByStatus(String status) {
        return bookingRepository.findByStatus(status);
    }

    @Transactional
    public void create(CreateRequest request) {
        validateDatetimes(request.startDatetime(), request.endDatetime());

        Customer customer = customerService.findById(request.customerId());
        ResourceService resourceService = findResourceServiceById(request.resourceServiceId());

        String token = request.confirmationToken() != null ?
                request.confirmationToken() :
                generateConfirmationToken();

        Booking booking = Booking.builder()
                .customer(customer)
                .resourceService(resourceService)
                .startDatetime(request.startDatetime())
                .endDatetime(request.endDatetime())
                .price(request.price())
                .confirmationToken(token)
                .notes(request.notes())
                .build();

        booking = bookingRepository.save(booking);

        BookingEmailDto emailDto = BookingEmailDto.fromEntity(booking);
        emailService.sendBookingConfirmationEmail(emailDto);
    }

    @Transactional
    public void update(Integer id, UpdateRequest request) {
        validateDatetimes(request.startDatetime(), request.endDatetime());
        validateStatus(request.status());

        Booking booking = findById(id);
        Customer customer = customerService.findById(request.customerId());
        ResourceService resourceService = findResourceServiceById(request.resourceServiceId());

        booking.setCustomer(customer);
        booking.setResourceService(resourceService);
        booking.setStartDatetime(request.startDatetime());
        booking.setEndDatetime(request.endDatetime());
        booking.setPrice(request.price());
        booking.setStatus(request.status());
        booking.setCancellationReason(request.cancellationReason());
        booking.setCancelledBy(request.cancelledBy());
        booking.setCancelledAt(request.cancelledAt());
        booking.setConfirmationToken(request.confirmationToken());
        booking.setNotes(request.notes());

        bookingRepository.save(booking);
    }

    @Transactional
    public void delete(Integer id) {
        Booking booking = findById(id);
        bookingRepository.delete(booking);
    }

    @Transactional
    public Booking createPublic(PublicBookingDto.CreateRequest request) {
        validateDatetimes(request.startDatetime(), request.endDatetime());

        Customer customer = customerService.findById(request.customerId());
        ResourceService resourceService = findResourceServiceById(request.resourceServiceId());

        Booking booking = Booking.builder()
                .customer(customer)
                .resourceService(resourceService)
                .startDatetime(request.startDatetime())
                .endDatetime(request.endDatetime())
                .price(request.price())
                .confirmationToken(generateConfirmationToken())
                .notes(request.notes())
                .build();

        booking = bookingRepository.save(booking);

        BookingEmailDto emailDto = BookingEmailDto.fromEntity(booking);
        emailService.sendBookingConfirmationEmail(emailDto);

        return booking;
    }

    @Transactional
    public void confirmBooking(String token) {
        Booking booking = findByConfirmationToken(token);

        switch (booking.getStatus()) {
            case "Confirmada", "Cancelada", "Completada" ->
                    throw new BadRequestException("No se puede confirmar una reserva " + booking.getStatus().toLowerCase());
            default -> {
                booking.setStatus("Confirmada");
                bookingRepository.save(booking);
            }
        }
    }

    @Transactional
    public void cancelBooking(String token, String reason) {
        Booking booking = findByConfirmationToken(token);

        switch (booking.getStatus()) {
            case "Cancelada", "Completada" ->
                    throw new BadRequestException("No se puede cancelar una reserva " + booking.getStatus().toLowerCase());
            default -> {
                booking.setStatus("Cancelada");
                booking.setCancellationReason(reason != null ? reason : "Cancelada por el cliente");
                booking.setCancelledBy("Cliente");
                booking.setCancelledAt(LocalDateTime.now());
                bookingRepository.save(booking);
            }
        }
    }

    private ResourceService findResourceServiceById(Integer id) {
        return resourceServiceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Recurso-Servicio no encontrado"));
    }

    private void validateDatetimes(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start) || end.isEqual(start)) {
            throw new BadRequestException("La fecha/hora de fin debe ser posterior a la de inicio");
        }
    }

    private void validateStatus(String status) {
        List<String> validStatuses = List.of("Pendiente", "Confirmada", "Completada", "Cancelada");
        if (!validStatuses.contains(status)) {
            throw new BadRequestException("Estado inválido. Valores permitidos: " + String.join(", ", validStatuses));
        }
    }

    private String generateConfirmationToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}