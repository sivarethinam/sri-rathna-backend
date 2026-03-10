package com.srirathna.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private Key getSigningKey() {
        // Pad secret to at least 64 characters for HS256
        String paddedSecret = secret;
        while (paddedSecret.length() < 64) {
            paddedSecret = paddedSecret + secret;
        }
        byte[] keyBytes = paddedSecret.substring(0, 64).getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String mobile, String role, String name) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("name", name);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(mobile)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractMobile(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return (String) extractClaims(token).get("role");
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = extractClaims(token);
            // Check not expired
            return claims.getExpiration().after(new Date());
        } catch (ExpiredJwtException e) {
            System.out.println("JWT expired: " + e.getMessage());
            return false;
        } catch (JwtException e) {
            System.out.println("JWT invalid: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("JWT error: " + e.getMessage());
            return false;
        }
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}