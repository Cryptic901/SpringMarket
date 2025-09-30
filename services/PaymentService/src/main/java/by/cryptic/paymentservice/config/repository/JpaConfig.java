package by.cryptic.paymentservice.config.repository;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "by.cryptic.paymentservice.repository.write")
@Profile("jpa")
@Configuration
public class JpaConfig {
}
