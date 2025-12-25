package by.cryptic.analyticservice.config.repository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@EnableRedisRepositories
@ConditionalOnProperty(name = "spring.cache.enabled", havingValue = "true")
@Configuration
public class RedisConfig {
}
