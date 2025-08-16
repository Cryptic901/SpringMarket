package by.cryptic.categoryservice.config;

import by.cryptic.categoryservice.model.write.Category;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
@RequiredArgsConstructor
@Profile("cache")
public class CacheConfig {

    private final ObjectMapper objectMapper;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory("redis", 6379);
    }

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory factory) {
        ObjectMapper cacheObjectMapper = objectMapper.copy();
        return RedisCacheManager.builder(factory)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
                        .disableCachingNullValues()
                        .entryTtl(Duration.ofDays(1))
                        .serializeKeysWith(RedisSerializationContext
                                .SerializationPair.fromSerializer(new StringRedisSerializer()))
                        .serializeValuesWith(RedisSerializationContext
                                .SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(cacheObjectMapper, Category.class))))
                .build();
    }
}
