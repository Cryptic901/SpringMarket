package by.cryptic.cartservice.service.command.it.retry;

import by.cryptic.cartservice.CartServiceApplication;
import by.cryptic.cartservice.client.ProductServiceAdapter;
import by.cryptic.cartservice.client.ProductServiceClient;
import by.cryptic.cartservice.repository.write.CartRepository;
import by.cryptic.cartservice.service.command.CartAddCommand;
import by.cryptic.cartservice.service.command.handler.CartAddCommandHandler;
import by.cryptic.exceptions.CreatingException;
import by.cryptic.utils.event.DomainEvent;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

/* Run Docker before start */
@SpringBootTest(
        classes = CartServiceApplication.class,
        properties = "spring.kafka.listener.auto-startup=false"
)
@ImportAutoConfiguration(exclude = KafkaAutoConfiguration.class)
@ActiveProfiles(value = {"test", "jpa"})
@Testcontainers
class CartAddCommandHandlerCircuitBreakerTest {

    @MockitoBean
    private CartRepository cartRepository;

    @MockitoBean
    private CacheManager cacheManager;

    @MockitoBean
    private Cache cache;

    @Container
    static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private CartAddCommandHandler cartAddCommandHandler;

    @Autowired
    private ProductServiceAdapter productServiceAdapter;

    @MockitoBean
    private ProductServiceClient productServiceClient;

    @MockitoBean
    private KafkaTemplate<String, DomainEvent> kafkaTemplate;

    @DynamicPropertySource
    static void propertySource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", postgreSQLContainer::getDriverClassName);
    }


    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    private CircuitBreaker circuitBreaker;

    @BeforeEach
    void setup() {
        circuitBreaker = circuitBreakerRegistry.circuitBreaker("productCircuitBreaker");
        circuitBreaker.reset();
    }

    @Test
    void repositoryFalls_thenCircuitBreakerFallbackAreTriggered() {
        //Arrange
        UUID productId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        CartAddCommand cartAddCommand = new CartAddCommand(productId, userId);

        when(productServiceClient.getProductById(productId))
                .thenThrow(new TransientDataAccessResourceException("DB down"));
        //Act
        assertThrows(CreatingException.class, () -> cartAddCommandHandler.handle(cartAddCommand));
        //Assert
        assertEquals("CB should be OPEN after errors", CircuitBreaker.State.OPEN, circuitBreaker.getState());
    }
}

