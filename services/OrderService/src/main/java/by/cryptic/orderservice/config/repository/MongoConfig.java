package by.cryptic.orderservice.config.repository;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackages = "by.cryptic.orderservice.repository.read")
@Profile("mongo")
@Configuration
public class MongoConfig {
}
