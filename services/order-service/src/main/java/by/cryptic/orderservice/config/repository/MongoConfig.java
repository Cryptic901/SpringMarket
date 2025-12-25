package by.cryptic.orderservice.config.repository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackages = "by.cryptic.orderservice.repository.read")
@ConditionalOnProperty(name = "spring.mongo.enabled", havingValue = "true")
@Configuration
public class MongoConfig {
}
