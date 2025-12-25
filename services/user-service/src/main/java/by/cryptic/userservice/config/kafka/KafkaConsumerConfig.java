package by.cryptic.userservice.config.kafka;

import by.cryptic.utils.event.DomainEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.Objects;

@Configuration
@EnableKafka
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true")
@Slf4j
public class KafkaConsumerConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<@NonNull String, @NonNull DomainEvent> kafkaListenerContainerFactory(
            ConsumerFactory<@NonNull String, @NonNull DomainEvent> consumerFactory, DeadLetterPublishingRecoverer deadLetterPublishingRecoverer) {
        ConcurrentKafkaListenerContainerFactory<@NonNull String, @NonNull DomainEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConcurrency(3);
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setGroupId("user-consumer-group");
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                deadLetterPublishingRecoverer,
                new FixedBackOff(1000, 5L)
        );
        errorHandler.setRetryListeners((record, ex, deliveryAttempt)
                -> log.warn("Retry attempt {} for record {} due to {}",
                deliveryAttempt, record, Objects.requireNonNull(ex).getMessage()));
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer
            (KafkaTemplate<@NonNull String, @NonNull DomainEvent> kafkaTemplate) {
        return new DeadLetterPublishingRecoverer(kafkaTemplate,
                (record, exception) -> {
                    String topicName = record.topic();
                    String dlqTopicName = topicName + ".DLQ";
                    return new TopicPartition(dlqTopicName, record.partition());
                });
    }
}