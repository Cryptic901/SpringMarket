package by.cryptic.cartservice.config.kafka;

import by.cryptic.utils.event.DomainEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
@Profile("kafka")
@EnableKafka
@Slf4j
public class KafkaConsumerConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<@NonNull String,@NonNull DomainEvent> kafkaListenerContainerFactory(
            ConsumerFactory<@NonNull String, @NonNull DomainEvent> consumerFactory, DeadLetterPublishingRecoverer deadLetterPublishingRecoverer) {
        ConcurrentKafkaListenerContainerFactory<@NonNull String, @NonNull DomainEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConcurrency(3);
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setGroupId("cart-consumer-group");
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

    @Bean("stringKafkaListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<@NonNull String, @NonNull String> stringKafkaListenerFactory() {

        ConcurrentKafkaListenerContainerFactory<@NonNull String, @NonNull String> listenerContainerFactory =
                new ConcurrentKafkaListenerContainerFactory<>();

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-1:9090,kafka-2:9090,kafka-3:9090");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "outbox-consumer-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        listenerContainerFactory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(props));
        return listenerContainerFactory;
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