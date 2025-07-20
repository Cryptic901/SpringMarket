package by.cryptic.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
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
                                .anyExchange().authenticated())
                .oauth2Login(Customizer.withDefaults())
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
                .build();
    }

    private static ReactiveJwtGrantedAuthoritiesConverterAdapter getReactiveJwtGrantedAuthoritiesConverterAdapter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("realm_access.roles");

        return new ReactiveJwtGrantedAuthoritiesConverterAdapter(jwtGrantedAuthoritiesConverter);
    }
}
