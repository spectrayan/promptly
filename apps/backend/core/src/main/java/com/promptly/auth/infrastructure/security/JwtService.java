package com.promptly.auth.infrastructure.security;

import com.promptly.auth.domain.model.User;
import com.promptly.shared.config.PromptlyProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT token generation and validation service.
 * Used in LOCAL auth mode to issue and verify self-signed tokens.
 */
@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    public JwtService(PromptlyProperties properties) {
        var jwt = properties.getAuth().getJwt();
        this.signingKey = Keys.hmacShaKeyFor(jwt.getSecret().getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMs = jwt.getExpirationMs();
        this.refreshTokenExpirationMs = jwt.getRefreshExpirationMs();
    }

    public String generateAccessToken(User user) {
        return buildToken(user, accessTokenExpirationMs, "access");
    }

    public String generateRefreshToken(User user) {
        return buildToken(user, refreshTokenExpirationMs, "refresh");
    }

    public String extractUserId(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public String extractEmail(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.get("email", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public long getAccessTokenExpirationMs() {
        return accessTokenExpirationMs;
    }

    private String buildToken(User user, long expirationMs, String tokenType) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(user.getId())
                .claim("email", user.getEmail())
                .claim("name", user.getDisplayName())
                .claim("orgRole", user.getOrgRole().name())
                .claim("type", tokenType)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
