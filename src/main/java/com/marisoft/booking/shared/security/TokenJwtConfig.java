package com.marisoft.booking.shared.security;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class TokenJwtConfig {

    public static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();
    public static final String PREFIX_TOKEN = "Bearer ";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    private static final Long ACCESS_TOKEN_EXPIRATION = 7200000L; // 2 horas
    private static final Long REFRESH_TOKEN_EXPIRATION = 2592000000L; // 30 días

    public SecretKey getSecretKey() {
        return SECRET_KEY;
    }

    public Long getAccessTokenExpiration() {
        return ACCESS_TOKEN_EXPIRATION;
    }

    public Long getRefreshTokenExpiration() {
        return REFRESH_TOKEN_EXPIRATION;
    }
}