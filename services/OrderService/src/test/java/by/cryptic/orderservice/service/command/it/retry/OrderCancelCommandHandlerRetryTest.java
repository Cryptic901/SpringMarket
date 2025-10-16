package by.cryptic.orderservice.service.command.it.retry;

import by.cryptic.exceptions.UpdatingException;
import by.cryptic.orderservice.OrderServiceApplication;
import by.cryptic.orderservice.client.CartServiceClient;
import by.cryptic.orderservice.client.ProductServiceClient;
import by.cryptic.orderservice.config.TestBeans;
import by.cryptic.orderservice.model.write.CustomerOrder;
import by.cryptic.orderservice.repository.write.CustomerOrderRepository;
import by.cryptic.orderservice.service.command.OrderCancelCommand;
import by.cryptic.orderservice.service.command.handler.OrderCancelCommandHandler;
import by.cryptic.utils.DTO.CartProductDTO;
import by.cryptic.utils.DTO.ProductDTO;
import by.cryptic.utils.enums.OrderStatus;
import by.cryptic.utils.event.DomainEvent;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
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
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
@Import(TestBeans.class)
@Testcontainers
class OrderCancelCommandHandlerRetryTest {

    @MockitoBean
    private CustomerOrderRepository orderRepository;

    @Container
    static PostgreSQLContainer<?> postgis =
            new PostgreSQLContainer<>(
                    DockerImageName.parse("postgis/postgis:latest")
                            .asCompatibleSubstituteFor("postgres")
            );

    @MockitoSpyBean
    private OrderCancelCommandHandler orderCancelCommandHandler;

    @MockitoBean
    private CartServiceClient cartServiceClient;

    @MockitoBean
    private ProductServiceClient productServiceClient;

    @Autowired
    private GeometryFactory geometryFactory;

    @MockitoBean
    private KafkaTemplate<String, DomainEvent> kafkaTemplate;

    @DynamicPropertySource
    static void propertySource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgis::getJdbcUrl);
        registry.add("spring.datasource.username", postgis::getUsername);
        registry.add("spring.datasource.password", postgis::getPassword);
        registry.add("spring.datasource.driver-class-name", postgis::getDriverClassName);
    }

    @Test
    void repositoryFalls_thenRetryFallbackAreTriggered() {
        //Arrange
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        CustomerOrder order = CustomerOrder.builder()
                .userId(userId)
                .userEmail("email@gmail.com")
                .products(new ArrayList<>())
                .createdBy(userId)
                .createdAt(LocalDateTime.now())
                .price(BigDecimal.ONE)
                .location(geometryFactory.createPoint(new Coordinate(21.22, 21.42)))
                .orderStatus(OrderStatus.COMPLETED)
                .paymentId(UUID.randomUUID())
                .build();

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        Mockito.when(cartServiceClient.getCartProductsByUserId(userId))
                .thenReturn(ResponseEntity.ok(List.of(
                CartProductDTO.builder()
                        .productId(UUID.randomUUID())
                        .pricePerUnit(BigDecimal.ONE)
                        .quantity(42)
                        .build())));
        Mockito.when(productServiceClient.getProductById(any())).thenReturn(ResponseEntity.ok(new ProductDTO("name",
                BigDecimal.ONE, 42, "desc",
                "img/url", UUID.randomUUID())));
        Mockito.doAnswer(invocation -> {
            throw new TransientDataAccessResourceException("DB down");
        }).when(orderRepository).findById(any());

        //Act
        assertThrows(UpdatingException.class, () -> orderCancelCommandHandler
                .handle(new OrderCancelCommand(orderId, userId, "asda@gmail.com")));
        //Assert
        verify(orderCancelCommandHandler, atLeast(1))
                .orderSaveCancelRetryFallback(any(), any(Throwable.class));
        verify(orderRepository, times(3)).findById(any());
    }
}
