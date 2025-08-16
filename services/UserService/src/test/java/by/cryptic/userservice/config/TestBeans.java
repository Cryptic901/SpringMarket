package by.cryptic.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.mock;

@Configuration
public class TestBeans {

    @Bean
    public JwtDecoder jwtDecoder() {
        return mock(JwtDecoder.class);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
