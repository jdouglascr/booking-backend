package com.marisoft.booking.shared.email;

import com.marisoft.booking.booking.Booking;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BookingEmailDto {
    private String confirmationToken;
    private String customerFirstName;
    private String customerLastName;
    private String customerEmail;
    private String serviceName;
    private String resourceName;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private Integer price;
    private String status;

    public static BookingEmailDto fromEntity(Booking booking) {
        return BookingEmailDto.builder()
                .confirmationToken(booking.getConfirmationToken())
                .customerFirstName(booking.getCustomer().getFirstName())
                .customerLastName(booking.getCustomer().getLastName())
                .customerEmail(booking.getCustomer().getEmail())
                .serviceName(booking.getResourceService().getService().getName())
                .resourceName(booking.getResourceService().getResource().getName())
                .startDatetime(booking.getStartDatetime())
                .endDatetime(booking.getEndDatetime())
                .price(booking.getPrice())
                .status(booking.getStatus())
                .build();
    }
}