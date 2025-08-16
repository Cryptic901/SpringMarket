package by.cryptic.productservice.service.command.it.retry;

import by.cryptic.productservice.model.write.Product;
import by.cryptic.productservice.repository.write.ProductRepository;
import by.cryptic.productservice.service.command.ProductDeleteCommand;
import by.cryptic.productservice.service.command.handler.ProductDeleteCommandHandler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/* Run Docker Desktop before start */
@SpringBootTest
@ActiveProfiles(value = {"test", "jpa"})
@Testcontainers
class ProductDeleteCommandHandlerRetryTest {

    @MockitoBean
    private ProductRepository productRepository;

    @Container
    static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    @MockitoBean
    private ProductDeleteCommandHandler productDeleteCommandHandler;

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
        UUID productId = UUID.randomUUID();
        Product product = Product.builder()
                .id(productId)
                .categoryId(categoryId)
                .name("testProduct")
                .description("testDesc")
                .image("test/img")
                .quantity(42)
                .createdBy(userId)
                .price(BigDecimal.TEN)
                .build();
        ProductDeleteCommand productDeleteCommand = new ProductDeleteCommand(productId, userId);

        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        Mockito.doThrow(new TransientDataAccessResourceException("DB down"))
                .when(productRepository).deleteById(productId);

        //Act
        assertThrows(Exception.class, () -> productDeleteCommandHandler.handle(productDeleteCommand));
        //Assert
        verify(productDeleteCommandHandler, atLeast(1))
                .productDeleteRetryFallback(eq(productDeleteCommand), any(Throwable.class));
        verify(productRepository, times(3)).deleteById(any());
        verify(productRepository, times(3)).findById(any());
    }
}
