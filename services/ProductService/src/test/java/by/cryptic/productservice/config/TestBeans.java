package by.cryptic.productservice.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestBeans {

    @Bean
    public CacheManager cacheManager() {
        return new SimpleCacheManager();
    }

}
