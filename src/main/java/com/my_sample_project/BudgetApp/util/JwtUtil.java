package com.my_sample_project.BudgetApp.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final Key SECRET_KEY;
    private static final long JWT_EXPIRATION = 1000 * 60 * 30; // 30 minutes expiration
//    private static final long JWT_EXPIRATION = 1000 * 10;

    // Initialize the SECRET_KEY using the value from properties or environment
    public JwtUtil(@Value("${jwt.secret.key}") String secretKey) {
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        this.SECRET_KEY = Keys.hmacShaKeyFor(decodedKey);
    }

    // Generate Access Token
    public String generateAccessToken(String username, Long userId) {
        return Jwts.builder()
            .setSubject(username)
            .claim("user_id", userId)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            .compact();
    }

    // Generate Refresh Token
    public String generateRefreshToken(String username, Long userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 1000 * 60 * 60 * 10);  // 10 hours expiration
//        Date expiration = new Date(now.getTime() + 1000 * 20);

        return Jwts.builder()
            .setSubject(username)
            .claim("user_id", userId)
            .setIssuedAt(now)
            .setExpiration(expiration)
            .signWith(SECRET_KEY)
            .compact();
    }

    // Validate the token (Access or Refresh)
    public boolean validateToken(String token, String username) {
        try {
            String extractedUsername = extractUsername(token);
            return extractedUsername.equals(username) && !isTokenExpired(token);
        } catch (Exception e) {
            // Handle any parsing errors
            System.err.println("JWT validation error: " + e.getMessage());
            return false;
        }
    }

    // Extracts the user ID from the JWT token from the "user_id" claim
    public Long extractUserId(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(SECRET_KEY)
            .parseClaimsJws(token)
            .getBody();

        Object userId = claims.get("user_id");

        if (userId == null) {
            throw new IllegalArgumentException("Invalid or missing user_id in token");
        }

        return Long.parseLong(userId.toString());
    }

    // Extract the username from the token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Helper method to extract userId from the Authorization token
    public Long extractUserIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header format.");
        }
        String token = authHeader.substring(7);

        return extractUserId(token);  // Extract userId from token
    }

    // Extract expiration date
    public Date extractExpiration(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(SECRET_KEY)
            .parseClaimsJws(token)
            .getBody();
        return claims.getExpiration();
    }

    // Check if the token has expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Extract any claim from the token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract all claims using the Jwts.parser() method
    public Claims extractAllClaims(String token) {
        try {
            if (token == null || token.isEmpty() || !token.contains(".")) {
                throw new IllegalArgumentException("Invalid JWT token format");
            }

            JwtParser parser = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .setAllowedClockSkewSeconds(5)  // Set the allowed clock skew to 5 seconds
                    .build();

            Claims claims = parser.parseClaimsJws(token).getBody();
            return claims;

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            System.err.println("JWT Token expired: " + e.getMessage());
            throw new RuntimeException("JWT Token expired", e);
        } catch (Exception e) {
            System.err.println("Error parsing JWT: " + e.getMessage());
            throw new RuntimeException("Invalid JWT token", e);
        }
    }
}