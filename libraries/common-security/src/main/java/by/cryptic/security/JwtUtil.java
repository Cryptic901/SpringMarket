package by.cryptic.security;

import by.cryptic.utils.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;

@RequiredArgsConstructor
@Component
public class JwtUtil {

    @Value("${spring.security.jwt.secret}")
    private String secret;

    @Value("${spring.security.jwt.expiration}")
    @Getter
    private Duration expiration;


    public SecretKey generateKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
    }

    public String buildToken(UserDetails userDetails, Map<String, Object> claims,
                             Duration expiration, UUID id, String email) {
        claims.put("email", email);
        claims.put("id", id);
        claims.put("authorities", userDetails.getAuthorities());
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plusSeconds(expiration.toSeconds())))
                .signWith(generateKey())
                .compact();
    }

    public String generateToken(UserDetails userDetails, Map<String, Object> claims, UUID id, String email) {
        return buildToken(userDetails, claims, expiration, id, email);
    }

    public String generateToken(UserDetails userDetails, UUID id, String email) {
        return generateToken(userDetails, new HashMap<>(), id, email);
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsHandler) {
        Claims claims = getAllClaimsFromToken(token);
        return claimsHandler.apply(claims);
    }

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    public String extractId(String token) {
        return extractClaims(token, Claims::getId);
    }

    public String extractEmail(String token) {
        return extractClaims(token, claims -> claims.get("email", String.class));
    }

    public Role extractRole(String token) {
        return extractClaims(token, claims -> claims.get("role", Role.class));
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(generateKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getExpiration().before(new Date());
        } catch (IllegalArgumentException | JwtException e) {
            return false;
        }
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
