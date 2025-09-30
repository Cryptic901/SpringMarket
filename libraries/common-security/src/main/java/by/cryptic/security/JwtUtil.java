package by.cryptic.security;

import by.cryptic.utils.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtUtil {

    public static UUID extractUserId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }

    public static String extractUsername(Jwt jwt) {
        return jwt.getClaimAsString("preferred_username");
    }

    public static boolean isTokenExpired(Jwt jwt) {
        return jwt.getClaimAsInstant("exp").isBefore(Instant.now());
    }

    public static String extractEmail(Jwt jwt) {
        return jwt.getClaimAsString("email");
    }

    public static Role extractRole(Jwt jwt) {
        for (String authority : jwt.getClaimAsStringList("realm_access.roles")) {
            if (authority.startsWith("ROLE_")) {
                return Role.valueOf(authority);
            }
        }
        return null;
    }

    public static boolean hasRole(Role role, Jwt jwt) {
        return Objects.equals(extractRole(jwt), role);
    }

    public static boolean isTokenValid(Jwt jwt) {
        return jwt.getTokenValue().chars().filter(ch -> ch == '.').count() == 2;
    }
}
