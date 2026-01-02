package by.cryptic.orderservice.config.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@ConditionalOnProperty(name = "spring.security.enabled", havingValue = "true")
@Configuration
public class SecurityConfig {

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(
                "http://keycloak.keycloak.svc.cluster.local/realms/springmarket/protocol/openid-connect/certs"
        ).build();

        OAuth2TokenValidator<Jwt> jwtOAuth2TokenValidator =
                JwtValidators.createDefaultWithIssuer(
                        "http://api.local/realms/springmarket"
                );

        jwtDecoder.setJwtValidator(jwtOAuth2TokenValidator);

        return jwtDecoder;
    }
}
