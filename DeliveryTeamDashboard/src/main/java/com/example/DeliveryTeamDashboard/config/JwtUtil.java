package com.example.DeliveryTeamDashboard.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private final Key secretKey;
    private final long expirationTime;

    public JwtUtil(@Value("${jwt.secret:#{null}}") String secret,
                   @Value("${jwt.expiration:36000000}") long expirationTime) {
        this.secretKey = secret != null ? Keys.hmacShaKeyFor(secret.getBytes()) : Keys.secretKeyFor(SignatureAlgorithm.HS512);
        this.expirationTime = expirationTime; // Default: 10 hours (36000000 ms)
    }

    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                logger.error("JWT token is null or empty");
                throw new IllegalArgumentException("JWT token cannot be null or empty");
            }
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            logger.warn("JWT token expired: {}", e.getMessage());
            throw new RuntimeException("JWT token is expired", e);
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token: {}", e.getMessage());
            throw new RuntimeException("JWT token is unsupported", e);
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token", e);
        } catch (SignatureException e) {
            logger.error("JWT signature verification failed: {}", e.getMessage());
            throw new RuntimeException("JWT signature verification failed", e);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            throw new RuntimeException("JWT token is invalid", e);
        }
    }

    public boolean isTokenValid(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            boolean isValid = extractedUsername.equals(username) && !isTokenExpired(token);
            if (!isValid) {
                logger.warn("Invalid token for user {}: username mismatch or expired", username);
            }
            return isValid;
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}