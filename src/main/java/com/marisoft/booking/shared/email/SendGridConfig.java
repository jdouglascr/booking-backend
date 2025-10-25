package com.marisoft.booking.shared.email;

import com.sendgrid.SendGrid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@Slf4j
public class SendGridConfig {

    @Value("${sendgrid.api-key}")
    private String apiKey;

    @Bean
    public SendGrid sendGrid() {
        if (!StringUtils.hasText(apiKey)) {
            log.error("SendGrid API Key no configurada");
            throw new IllegalStateException("SendGrid API Key es requerida");
        }

        log.info("SendGrid configurado correctamente");
        return new SendGrid(apiKey);
    }
}