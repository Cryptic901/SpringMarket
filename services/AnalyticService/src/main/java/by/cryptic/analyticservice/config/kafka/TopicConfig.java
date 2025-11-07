package by.cryptic.analyticservice.config.kafka;

import by.cryptic.utils.properties.KafkaTopicsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import java.util.Map;

@Configuration
@Profile("kafka")
@RequiredArgsConstructor
@Slf4j
public class TopicConfig {

    private final KafkaTopicsProperties props;
    private final KafkaAdmin kafkaAdmin;

    @EventListener(ApplicationReadyEvent.class)
    public void createTopics() {
        if (props.getName() == null || props.getDlq() == null || props.getDlq().getName() == null) {
            log.warn("Kafka topics not configured. Skipping creation.");
            return;
        }

        NewTopic main = TopicBuilder.name(props.getName())
                .partitions(props.getPartitions())
                .replicas(props.getReplicas())
                .config("min.insync.replicas", String.valueOf(props.getMinInsyncReplicas()))
                .build();

        NewTopic dlq = TopicBuilder.name(props.getDlq().getName())
                .partitions(props.getDlq().getPartitions())
                .replicas(props.getDlq().getReplicas())
                .config("min.insync.replicas", String.valueOf(props.getDlq().getMinInsyncReplicas()))
                .build();

        kafkaAdmin.createOrModifyTopics(main, dlq);
        log.info("Kafka topics created: {} and {}", main.name(), dlq.name());
    }
}