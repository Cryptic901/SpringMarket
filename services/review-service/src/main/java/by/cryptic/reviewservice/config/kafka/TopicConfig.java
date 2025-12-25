package by.cryptic.reviewservice.config.kafka;

import by.cryptic.utils.properties.KafkaTopicsProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true")
public class TopicConfig {

    private final KafkaTopicsProperties kafkaTopicsProperties;

    @Bean
    public NewTopic reviewTopic() {
        return TopicBuilder.name(kafkaTopicsProperties.getName())
                .partitions(kafkaTopicsProperties.getPartitions())
                .replicas(kafkaTopicsProperties.getReplicas())
                .configs(Map.of("min.insync.replicas", String.valueOf(kafkaTopicsProperties.getMinInsyncReplicas())))
                .build();
    }

    @Bean
    public NewTopic reviewTopicDlq() {
        if (kafkaTopicsProperties == null) {
            throw new IllegalStateException("DLQ config not ready");
        }
        return TopicBuilder.name(kafkaTopicsProperties.getDlq().getName())
                .partitions(kafkaTopicsProperties.getDlq().getPartitions())
                .replicas(kafkaTopicsProperties.getDlq().getReplicas())
                .configs(Map.of("min.insync.replicas", String.valueOf(kafkaTopicsProperties.getDlq().getMinInsyncReplicas())))
                .build();
    }
}