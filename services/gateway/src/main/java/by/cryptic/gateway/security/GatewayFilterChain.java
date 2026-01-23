package by.cryptic.gateway.security;

import by.cryptic.gateway.filter.AuthLoggingFilter;
import by.cryptic.gateway.handler.GatewayAccessDeniedHandler;
import by.cryptic.gateway.handler.GatewayAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.ExpressionJwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtGrantedAuthoritiesConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Slf4j
@Configuration
@Order(-2)
@RequiredArgsConstructor
@EnableWebFluxSecurity
public class GatewayFilterChain {

    private final GatewayAccessDeniedHandler customAccessDeniedHandler;
    private final GatewayAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http,
                                                      AuthLoggingFilter authLoggingFilter) {
        return http
                .authorizeExchange(auth ->
                        auth.pathMatchers("/login/**", "/oauth2/**", "/actuator/**",
                                        "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**",
                                        "/webjars/**", "/*/v3/api-docs", "/**-service/v3/api-docs",
                                        "/v3/api-docs/swagger-config").permitAll()
                                .pathMatchers(HttpMethod.DELETE, "/api/v1/categories/**", "/api/v1/warehouses/**").hasRole("ADMIN")
                                .pathMatchers(HttpMethod.POST, "/api/v1/categories/**", "/api/v1/warehouses/**").hasRole("ADMIN")
                                .pathMatchers(HttpMethod.PATCH, "/api/v1/categories/**", "/api/v1/warehouses/**").hasRole("ADMIN")
                                .pathMatchers(HttpMethod.PUT, "/api/v1/categories/**", "/api/v1/warehouses/**").hasRole("ADMIN")
                                .pathMatchers("/api/v1/admin/**").hasRole("ADMIN")
                                .anyExchange().authenticated())
                .oauth2ResourceServer(configurer ->
                        configurer.authenticationEntryPoint(customAuthenticationEntryPoint)
                                .jwt(
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
                .addFilterAfter(authLoggingFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(customAuthenticationEntryPoint)
                                .accessDeniedHandler(customAccessDeniedHandler))
                .build();
    }

    private static ReactiveJwtGrantedAuthoritiesConverterAdapter getReactiveJwtGrantedAuthoritiesConverterAdapter() {
        ExpressionJwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new ExpressionJwtGrantedAuthoritiesConverter(
                new SpelExpressionParser().parseRaw("[realm_access][roles]")
        );
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        return new ReactiveJwtGrantedAuthoritiesConverterAdapter(jwtGrantedAuthoritiesConverter);
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        NimbusReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder.withJwkSetUri(
                "http://keycloak.auth.svc.cluster.local/realms/springmarket/protocol/openid-connect/certs"
        ).build();

        OAuth2TokenValidator<Jwt> validator =
                JwtValidators.createDefaultWithIssuer("http://auth.local/realms/springmarket");
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(validator));
        return token -> decoder.decode(token).onErrorMap(
                IllegalStateException.class, ex -> {
                    if (ex.getMessage() != null && ex.getMessage().contains("Could not obtain the keys")) {
                        log.error("Failed to obtain JWK keys from Keycloak", ex);
                        return new AuthenticationServiceException("Authentication service unavailable:" +
                                " cannot obtain keys from Keycloak", ex);
                    }
                    log.error("Unexcepted IllegalStateException during JWT decoding", ex);
                    return new AuthenticationServiceException("JWT validation failed", ex);
                }
        )
                .onErrorMap(JwtException.class, ex -> {
                    if (ex.getMessage() != null) {
                        if (ex.getMessage().contains("expired")) {
                            log.warn("JWT token expired");
                            return new BadCredentialsException("Token has expired", ex);
                        } else if (ex.getMessage().contains("signature")) {
                         log.warn("JWT signature validation failed");
                         return new BadCredentialsException("Invalid token signature", ex);
                        }
                    }
                    log.warn("JWT validation failed {}", ex.getMessage());
                    return new BadCredentialsException("Invalid token", ex);
                })
                .doOnError(error -> log.error("JWT decoding error: {} - {}",
                        error.getClass().getSimpleName(),
                        error.getMessage()));
    }
}
