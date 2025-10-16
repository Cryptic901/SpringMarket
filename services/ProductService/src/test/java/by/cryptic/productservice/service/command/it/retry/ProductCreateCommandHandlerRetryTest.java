package by.cryptic.productservice.service.command.it.retry;

import by.cryptic.exceptions.CreatingException;
import by.cryptic.productservice.ProductServiceApplication;
import by.cryptic.productservice.client.CategoryServiceClient;
import by.cryptic.productservice.client.InventoryServiceClient;
import by.cryptic.productservice.repository.write.ProductRepository;
import by.cryptic.productservice.service.command.ProductCreateCommand;
import by.cryptic.productservice.service.command.handler.ProductCreateCommandHandler;
import by.cryptic.utils.DTO.CategoryDTO;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/* Run Docker before start */
@SpringBootTest(
        classes = ProductServiceApplication.class,
        properties = "spring.kafka.listener.auto-startup=false"
)
@ImportAutoConfiguration(exclude = KafkaAutoConfiguration.class)
@ActiveProfiles(value = {"test", "jpa"})
@Testcontainers
class ProductCreateCommandHandlerRetryTest {

    @MockitoBean
    private ProductRepository productRepository;

    @Container
    static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    @MockitoSpyBean
    private ProductCreateCommandHandler productCreateCommandHandler;

    @MockitoBean
    private CategoryServiceClient categoryServiceClient;

    @MockitoBean
    private InventoryServiceClient inventoryServiceClient;

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
        UUID categoryId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        ProductCreateCommand productCreateCommand = new ProductCreateCommand(
                "testTitle", userId, BigDecimal.TEN, 42,
                "testDesc", "img", categoryId
        );
        when(categoryServiceClient.getCategoryById(categoryId)).thenReturn(ResponseEntity.ok(
                new CategoryDTO("name", "desc")));
        Mockito.doThrow(new TransientDataAccessResourceException("DB down"))
                .when(productRepository).save(any());

        //Act
        assertThrows(CreatingException.class, () -> productCreateCommandHandler.handle(productCreateCommand));
        //Assert
        verify(productCreateCommandHandler, atLeast(1))
                .productCreateRetryFallback(eq(productCreateCommand), any(Throwable.class));
    }
}
