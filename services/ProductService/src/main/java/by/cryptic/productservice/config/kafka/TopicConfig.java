package by.cryptic.productservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.Map;

@Configuration
public class TopicConfig {

    @Bean
    public NewTopic productTopic() {
        return TopicBuilder.name("product-topic")
                .partitions(3)
                .replicas(3)
                .configs(Map.of("min.insync.replicas", "1"))
                .build();
    }

    @Bean
    public NewTopic categoryTopic() {
        return TopicBuilder.name("category-topic")
                .partitions(2)
                .replicas(2)
                .configs(Map.of("min.insync.replicas", "1"))
                .build();
    }

    @Bean
    public NewTopic productTopicDlq() {
        return TopicBuilder.name("product-topic.DLQ")
                .partitions(3)
                .replicas(3)
                .configs(Map.of("min.insync.replicas", "1"))
                .build();
    }

    @Bean
    public NewTopic categoryTopicDlq() {
        return TopicBuilder.name("category-topic.DLQ")
                .partitions(2)
                .replicas(2)
                .configs(Map.of("min.insync.replicas", "1"))
                .build();
    }
}