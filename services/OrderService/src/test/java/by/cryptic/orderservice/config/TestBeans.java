package by.cryptic.orderservice.config;

import org.locationtech.jts.geom.GeometryFactory;
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

    @Bean
    public GeometryFactory geometryFactory() {
        return new GeometryFactory();
    }

}
