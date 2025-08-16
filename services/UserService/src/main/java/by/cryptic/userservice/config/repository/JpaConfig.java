package by.cryptic.userservice.config.repository;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "by.cryptic.userservice.repository.write")
@Profile("jpa")
@Configuration
public class JpaConfig {
}
