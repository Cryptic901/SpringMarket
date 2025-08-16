package by.cryptic.sagaservice;

import by.cryptic.utils.properties.KafkaTopicsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(KafkaTopicsProperties.class)
public class SagaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SagaServiceApplication.class, args);
    }

}
