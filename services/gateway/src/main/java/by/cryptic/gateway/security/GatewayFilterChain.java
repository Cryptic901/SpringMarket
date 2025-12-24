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
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
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
                "http://keycloak.keycloak.svc.cluster.local:8080/realms/springmarket/protocol/openid-connect/certs"
        ).build();

        OAuth2TokenValidator<Jwt> validator =
                JwtValidators.createDefaultWithIssuer("http://localhost:9000/realms/springmarket");
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(validator));
        return decoder;
    }
}
