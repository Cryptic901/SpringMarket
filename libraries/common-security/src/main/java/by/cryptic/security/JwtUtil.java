package by.cryptic.security;

import by.cryptic.utils.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class JwtUtil {

    public static UUID extractUserId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }

    public static String extractUsername(Jwt jwt) {
        return jwt.getClaimAsString("preferred_username");
    }

    public static String extractEmail(Jwt jwt) {
        return jwt.getClaimAsString("email");
    }

    public static Role extractRole(Jwt jwt) {
        for (String authority : jwt.getClaimAsStringList("realm_access.roles")) {
            if(authority.startsWith("ROLE_")) {
                return Role.valueOf(authority);
            }
        }
        return null;
    }
}
