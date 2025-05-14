package by.cryptic.springmarket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.UUID;

@Configuration
public class AppConfig {

    @Bean
    public AuditorAware<UUID> auditorProvider() {
        return new AuditAware();
    }
}
