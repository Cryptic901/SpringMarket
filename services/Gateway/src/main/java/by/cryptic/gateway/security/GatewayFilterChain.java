package by.cryptic.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtGrantedAuthoritiesConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class GatewayFilterChain {

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(auth ->
                        auth.pathMatchers("/login/**", "/oauth2/**", "/actuator/**").permitAll()
                                .pathMatchers(HttpMethod.DELETE, "/api/v1/categories/**").hasRole("ADMIN")
                                .pathMatchers(HttpMethod.POST, "/api/v1/categories/**").hasRole("ADMIN")
                                .pathMatchers(HttpMethod.PATCH, "/api/v1/categories/**").hasRole("ADMIN")
                                .pathMatchers(HttpMethod.PUT, "/api/v1/categories/**").hasRole("ADMIN")
                                .anyExchange().authenticated())
                .oauth2ResourceServer(configurer ->
                        configurer.jwt(
                                jwt -> {
                                    ReactiveJwtAuthenticationConverter converter =
                                            new ReactiveJwtAuthenticationConverter();
                                    converter.setPrincipalClaimName("preferred_username");

                                    ReactiveJwtGrantedAuthoritiesConverterAdapter grantedAuthoritiesConverter =
                                            getReactiveJwtGrantedAuthoritiesConverterAdapter();
                                    converter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
                                    jwt.jwtAuthenticationConverter(converter);
                                }
                        ))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }

    private static ReactiveJwtGrantedAuthoritiesConverterAdapter getReactiveJwtGrantedAuthoritiesConverterAdapter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("realm_access.roles");

        return new ReactiveJwtGrantedAuthoritiesConverterAdapter(jwtGrantedAuthoritiesConverter);
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        NimbusReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder.withJwkSetUri(
                "http://keycloak:8080/realms/springmarket/protocol/openid-connect/certs"
        ).build();

        OAuth2TokenValidator<Jwt> validator =
                JwtValidators.createDefaultWithIssuer("http://localhost:9000/realms/springmarket");
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(validator));
        return decoder;
    }
}
