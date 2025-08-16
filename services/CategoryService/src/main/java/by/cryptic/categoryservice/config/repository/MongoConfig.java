package by.cryptic.categoryservice.config.repository;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackages = "by.cryptic.categoryservice.repository.read")
@Profile("mongo")
@Configuration
public class MongoConfig {
}
