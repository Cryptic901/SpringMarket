package by.cryptic.utils.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.kafka.topics")
@Data
public class KafkaTopicsProperties {
    private String name;
    private int partitions;
    private int replicas;
    private int minInsyncReplicas;
    private Dlq dlq;

    @Data
    public static class Dlq {
        private String name;
        private int partitions;
        private int replicas;
        private int minInsyncReplicas;
    }
}
