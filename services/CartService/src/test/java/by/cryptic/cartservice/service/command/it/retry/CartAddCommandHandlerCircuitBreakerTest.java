package by.cryptic.cartservice.service.command.it.retry;

import by.cryptic.cartservice.CartServiceApplication;
import by.cryptic.cartservice.client.ProductServiceClient;
import by.cryptic.cartservice.repository.write.CartRepository;
import by.cryptic.cartservice.service.command.CartAddCommand;
import by.cryptic.cartservice.service.command.handler.CartAddCommandHandler;
import by.cryptic.exceptions.CreatingException;
import by.cryptic.utils.DTO.ProductDTO;
import by.cryptic.utils.event.DomainEvent;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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

    @MockitoSpyBean
    private CartAddCommandHandler cartAddCommandHandler;

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

    @Test
    void repositoryFalls_thenCircuitBreakerFallbackAreTriggered() {
        //Arrange
        UUID productId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        CartAddCommand cartAddCommand = new CartAddCommand(productId, userId);
        ProductDTO productDTO = new ProductDTO("name", BigDecimal.ONE, 42, "desc",
                "img/url", UUID.randomUUID());

        Mockito.when(productServiceClient.getProductById(productId)).thenReturn(ResponseEntity.ok(productDTO));
        Mockito.doThrow(new TransientDataAccessResourceException("DB down"))
                .when(cartRepository).save(any());
        //Act
        assertThrows(CreatingException.class, () -> cartAddCommandHandler.getOrCreateCart(cartAddCommand));
        //Assert
        verify(cartAddCommandHandler, atLeast(1))
                .cartCreatingRetryFallback(eq(cartAddCommand), any(Throwable.class));
        verify(cartRepository, times(1)).save(any());
    }
}

