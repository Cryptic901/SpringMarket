package by.cryptic.orderservice.config.kafka;

import by.cryptic.utils.event.DomainEvent;
import lombok.NonNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true")
public class KafkaProducerConfig {

    @Bean
    public KafkaTemplate<@NonNull String, @NonNull DomainEvent> kafkaTemplate(ProducerFactory<@NonNull String, @NonNull DomainEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}