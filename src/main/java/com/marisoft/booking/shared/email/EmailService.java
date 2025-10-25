package com.marisoft.booking.shared.email;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
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

    private final SendGrid sendGrid;

    @Value("${sendgrid.from-email}")
    private String fromEmail;

    @Value("${sendgrid.from-name:Sistema de Reservas}")
    private String fromName;

    @Value("${FRONTEND_URL}")
    private String frontendUrl;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Async
    public void sendBookingConfirmationEmail(BookingEmailDto bookingDto, String logoUrl, String bannerUrl) {
        try {
            String htmlContent = buildEmailContent(bookingDto, logoUrl, bannerUrl);
            String subject = "Confirma tu reserva - " + bookingDto.getServiceName();

            sendHtmlEmail(bookingDto.getCustomerEmail(), subject, htmlContent);

            log.info("Email de confirmación enviado exitosamente a: {}", bookingDto.getCustomerEmail());
        } catch (IOException e) {
            log.error("Error al enviar email de confirmación a {}: {}",
                    bookingDto.getCustomerEmail(), e.getMessage(), e);
        }
    }

    public void sendHtmlEmail(String toEmail, String subject, String htmlContent) throws IOException {
        Email from = new Email(fromEmail, fromName);
        Email to = new Email(toEmail);
        Content content = new Content("text/html", htmlContent);

        Mail mail = new Mail(from, subject, to, content);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);

            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                log.debug("Email enviado exitosamente. Status: {}", response.getStatusCode());
            } else {
                log.error("Error al enviar email. Status: {}, Body: {}",
                        response.getStatusCode(), response.getBody());
                throw new IOException("SendGrid retornó status: " + response.getStatusCode());
            }
        } catch (IOException e) {
            log.error("Error en la comunicación con SendGrid: {}", e.getMessage());
            throw e;
        }
    }

    private String buildEmailContent(BookingEmailDto booking, String logoUrl, String bannerUrl) throws IOException {
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

        assert logoUrl != null && bannerUrl != null;
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
                .replace("{{cancelUrl}}", cancelUrl)
                .replace("{{logoUrl}}", logoUrl)
                .replace("{{bannerUrl}}", bannerUrl);
    }

    private String loadTemplate() throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/booking-confirmation.html");
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}