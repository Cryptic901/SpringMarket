package by.cryptic.orderservice.service.command.it.retry;

import by.cryptic.exceptions.CreatingException;
import by.cryptic.orderservice.OrderServiceApplication;
import by.cryptic.orderservice.client.CartServiceClient;
import by.cryptic.orderservice.client.ProductServiceClient;
import by.cryptic.orderservice.model.write.CustomerOrder;
import by.cryptic.orderservice.repository.write.CustomerOrderRepository;
import by.cryptic.orderservice.service.command.OrderCreateCommand;
import by.cryptic.orderservice.service.command.handler.OrderCreateCommandHandler;
import by.cryptic.utils.DTO.CartProductDTO;
import by.cryptic.utils.DTO.ProductDTO;
import by.cryptic.utils.enums.OrderStatus;
import by.cryptic.utils.enums.PaymentMethod;
import by.cryptic.utils.event.DomainEvent;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/* Run Docker Desktop before start */
@SpringBootTest(
        classes = OrderServiceApplication.class,
        properties = "spring.kafka.listener.auto-startup=false"
)
@ImportAutoConfiguration(exclude = KafkaAutoConfiguration.class)
@ActiveProfiles(value = {"test", "jpa"})
@Testcontainers
class OrderCreateCommandHandlerCircuitBreakerTest {

    @MockitoBean
    private CustomerOrderRepository orderRepository;

    @Container
    static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    @MockitoSpyBean
    private OrderCreateCommandHandler orderCreateCommandHandler;

    @MockitoBean
    private CartServiceClient cartServiceClient;

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
    void repositoryFalls_thenRetryFallbackAreTriggered() {
        //Arrange
        UUID userId = UUID.randomUUID();
        CustomerOrder order = CustomerOrder.builder()
                .userId(userId)
                .userEmail("email@gmail.com")
                .products(new ArrayList<>())
                .createdBy(userId)
                .createdAt(LocalDateTime.now())
                .price(BigDecimal.ONE)
                .location("location")
                .orderStatus(OrderStatus.PENDING)
                .paymentId(UUID.randomUUID())
                .build();

        OrderCreateCommand orderCreateCommand = new OrderCreateCommand(
                "testTitle", PaymentMethod.APPLE_PAY, userId, "emaul@gmail.com");

        Mockito.when(cartServiceClient.getCartProductsByUserId(userId)).thenReturn(ResponseEntity.ok(List.of(
                CartProductDTO.builder()
                        .productId(UUID.randomUUID())
                        .pricePerUnit(BigDecimal.ONE)
                        .quantity(42)
                        .build())));
        Mockito.when(productServiceClient.getProductById(any())).thenReturn(ResponseEntity.ok(new ProductDTO("name",
                BigDecimal.ONE, 42, "desc",
                "img/url", UUID.randomUUID())));
        Mockito.doThrow(new TransientDataAccessResourceException("DB down"))
                .when(orderRepository).save(any());

        //Act
        assertThrows(CreatingException.class, () -> orderCreateCommandHandler.saveOrderWithCircuitBreaker(order, orderCreateCommand));
        //Assert
        verify(orderCreateCommandHandler, atLeast(1))
                .orderCreateCircuitBreakerFallback(eq(order), eq(orderCreateCommand), any(Throwable.class));
        verify(orderRepository, atLeast(1)).save(any());
    }
}
