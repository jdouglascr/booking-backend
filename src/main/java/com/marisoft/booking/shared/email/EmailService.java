package com.marisoft.booking.shared.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${MAIL_USERNAME}")
    private String fromEmail;

    @Value("${FRONTEND_URL}")
    private String frontendUrl;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Async
    public void sendBookingConfirmationEmail(BookingEmailDto bookingDto) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(bookingDto.getCustomerEmail());
            helper.setSubject("Confirma tu reserva - " + bookingDto.getServiceName());
            helper.setText(buildEmailContent(bookingDto), true);

            mailSender.send(message);
            log.info("Email de confirmación enviado a: {}", bookingDto.getCustomerEmail());
        } catch (MessagingException | IOException e) {
            log.error("Error al enviar email de confirmación", e);
        }
    }

    private String buildEmailContent(BookingEmailDto booking) throws IOException {
        String template = loadTemplate();

        String confirmUrl = frontendUrl + "booking/confirm/" + booking.getConfirmationToken();
        String cancelUrl = frontendUrl + "booking/cancel/" + booking.getConfirmationToken();

        String customerName = booking.getCustomerFirstName() + " " + booking.getCustomerLastName();
        String serviceName = booking.getServiceName();
        String resourceName = booking.getResourceName();
        String date = booking.getStartDatetime().format(DATE_FORMATTER);
        String startTime = booking.getStartDatetime().format(TIME_FORMATTER);
        String endTime = booking.getEndDatetime().format(TIME_FORMATTER);
        String price = NumberFormat.getCurrencyInstance(Locale.of("es", "CL")).format(booking.getPrice());

        return template
                .replace("{{customerName}}", customerName)
                .replace("{{serviceName}}", serviceName)
                .replace("{{resourceName}}", resourceName)
                .replace("{{date}}", date)
                .replace("{{startTime}}", startTime)
                .replace("{{endTime}}", endTime)
                .replace("{{price}}", price)
                .replace("{{status}}", booking.getStatus())
                .replace("{{confirmUrl}}", confirmUrl)
                .replace("{{cancelUrl}}", cancelUrl);
    }

    private String loadTemplate() throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/booking-confirmation.html");
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}