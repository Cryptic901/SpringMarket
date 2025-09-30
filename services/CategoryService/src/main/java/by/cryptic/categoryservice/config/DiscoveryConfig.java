package by.cryptic.categoryservice.config;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@EnableDiscoveryClient
@Profile("eureka")
@Configuration
public class DiscoveryConfig {
}
