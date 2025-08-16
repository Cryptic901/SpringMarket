package by.cryptic.cartservice.config.kafka;

import by.cryptic.utils.event.DomainEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
@Profile("kafka")
public class KafkaProducerConfig {

    @Bean
    public KafkaTemplate<String, DomainEvent> kafkaTemplate(ProducerFactory<String, DomainEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}