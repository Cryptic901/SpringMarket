package by.cryptic.productservice.service.command.it.retry;

import by.cryptic.productservice.model.write.Product;
import by.cryptic.productservice.repository.write.ProductRepository;
import by.cryptic.productservice.service.command.ProductUpdateCommand;
import by.cryptic.productservice.service.command.handler.ProductUpdateCommandHandler;
import by.cryptic.utils.ProductStatus;
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
class ProductUpdateCommandHandlerRetryTest {

    @MockitoBean
    private ProductRepository productRepository;

    @Container
    static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    @MockitoBean
    private ProductUpdateCommandHandler productUpdateCommandHandler;

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
        UUID categoryId = UUID.randomUUID();
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
        ProductUpdateCommand productUpdateCommand = new ProductUpdateCommand(productId,
                "newTitle", null, null, null, null, userId, categoryId, ProductStatus.ACTIVE);

        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        Mockito.doThrow(new TransientDataAccessResourceException("DB down"))
                .when(productRepository).save(product);
        //Act
        assertThrows(Exception.class, () -> productUpdateCommandHandler.handle(productUpdateCommand));
        //Assert
        verify(productUpdateCommandHandler, atLeast(1))
                .productUpdateRetryFallback(eq(productUpdateCommand), any(Throwable.class));
        verify(productRepository, times(3)).findById(any());
    }
}
