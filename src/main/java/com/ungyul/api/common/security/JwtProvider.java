package com.ungyul.api.common.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    private final SecretKey key;
    private final long accessTokenExpirationMs;

    public JwtProvider(
            @Value("${auth.jwt.secret}") String secret,
            @Value("${auth.jwt.access-token-expiration-ms}") long accessTokenExpirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMs = accessTokenExpirationMs;
    }

    public String generateAccessToken(Long userId) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessTokenExpirationMs))
                .signWith(key)
                .compact();
    }

    public Long getUserId(String token) {
        return Long.parseLong(
                Jwts.parser().verifyWith(key).build()
                        .parseSignedClaims(token)
                        .getPayload()
                        .getSubject());
    }

    public boolean validate(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
