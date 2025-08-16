package by.cryptic.analyticservice;

import by.cryptic.utils.properties.KafkaTopicsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@EnableRedisRepositories
@EnableConfigurationProperties(KafkaTopicsProperties.class)
public class AnalyticServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnalyticServiceApplication.class, args);
    }

}
