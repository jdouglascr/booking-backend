package com.marisoft.booking.shared.security;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class TokenJwtConfig {

    public static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();
    public static final String PREFIX_TOKEN = "Bearer ";
    public static final String HEADER_AUTHORIZATION = "Authorization";

    public SecretKey getSecretKey() {
        return SECRET_KEY;
    }

    public Long getExpiration() {
        return 7200000L;
    }
}
