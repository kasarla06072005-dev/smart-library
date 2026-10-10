package com.anurag.library.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    // Secret comes from the JWT_SECRET environment variable. The fallback below
    // exists ONLY so the app can start without extra setup on a first local run -
    // it is intentionally obvious and MUST be overridden before any real deployment.
    // See AdminSeeder / README_DEPLOYMENT.md for how to set this.
    @Value("${app.jwt.secret:INSECURE_DEV_ONLY_CHANGE_ME_0123456789abcdef}")
    private String secretKey;

    @Value("${app.jwt.expiration-minutes:30}")
    private long expirationMinutes;

    @PostConstruct
    void warnIfDefaultSecret() {
        if (secretKey.startsWith("INSECURE_DEV_ONLY")) {
            log.warn("=============================================================");
            log.warn(" JWT_SECRET is not set - using the built-in development key.");
            log.warn(" This is NOT safe for any public deployment. Set the JWT_SECRET");
            log.warn(" environment variable to a long random string before deploying.");
            log.warn("=============================================================");
        }
        if (secretKey.length() < 32) {
            throw new IllegalStateException("app.jwt.secret must be at least 32 characters long");
        }
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * expirationMinutes))
                .signWith(getKey())
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
