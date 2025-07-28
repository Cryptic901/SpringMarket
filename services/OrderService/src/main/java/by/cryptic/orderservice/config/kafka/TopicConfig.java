package by.cryptic.orderservice.config.kafka;

import by.cryptic.utils.properties.KafkaTopicsProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class TopicConfig {

    private final KafkaTopicsProperties kafkaTopicsProperties;

    @Bean
    public NewTopic orderTopic() {
        return TopicBuilder.name(kafkaTopicsProperties.getName())
                .partitions(kafkaTopicsProperties.getPartitions())
                .replicas(kafkaTopicsProperties.getReplicas())
                .configs(Map.of("min.insync.replicas", String.valueOf(kafkaTopicsProperties.getMinInsyncReplicas())))
                .build();
    }

    @Bean
    public NewTopic orderTopicDlq() {
        return TopicBuilder.name(kafkaTopicsProperties.getDlq().getName())
                .partitions(kafkaTopicsProperties.getDlq().getPartitions())
                .replicas(kafkaTopicsProperties.getDlq().getReplicas())
                .configs(Map.of("min.insync.replicas", String.valueOf(kafkaTopicsProperties.getDlq().getMinInsyncReplicas())))
                .build();
    }
}