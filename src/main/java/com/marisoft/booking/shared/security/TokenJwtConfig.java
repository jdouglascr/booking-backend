package com.marisoft.booking.shared.security;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class TokenJwtConfig {

    private final SecretKey secretKey;
    public static final String PREFIX_TOKEN = "Bearer ";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    private static final Long ACCESS_TOKEN_EXPIRATION = 7200000L;
    private static final Long REFRESH_TOKEN_EXPIRATION = 2592000000L;

    public TokenJwtConfig(@Value("${JWT_SECRET_KEY}") String secretKeyString) {
        if (secretKeyString == null || secretKeyString.length() < 32) {
            throw new IllegalArgumentException(
                    "JWT_SECRET_KEY debe tener al menos 32 caracteres"
            );
        }
        this.secretKey = Keys.hmacShaKeyFor(
                secretKeyString.getBytes(StandardCharsets.UTF_8)
        );
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public Long getAccessTokenExpiration() {
        return ACCESS_TOKEN_EXPIRATION;
    }

    public Long getRefreshTokenExpiration() {
        return REFRESH_TOKEN_EXPIRATION;
    }
}