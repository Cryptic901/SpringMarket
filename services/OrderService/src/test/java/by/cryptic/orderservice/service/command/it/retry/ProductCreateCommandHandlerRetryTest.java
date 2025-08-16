//package by.cryptic.orderservice.service.command.it.retry;
//
//import by.cryptic.productservice.repository.write.ProductRepository;
//import by.cryptic.productservice.service.command.product.ProductCreateCommand;
//import by.cryptic.productservice.service.command.product.handler.ProductCreateCommandHandler;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.dao.TransientDataAccessResourceException;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import java.math.BigDecimal;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//
///* Run Docker Desktop before start */
//@SpringBootTest
//@ActiveProfiles(value = {"test", "jpa"})
//@Testcontainers
//class ProductCreateCommandHandlerRetryTest {
//
//    @MockitoBean
//    private ProductRepository productRepository;
//
//    @Container
//    static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine");
//
//    @MockitoSpyBean
//    private ProductCreateCommandHandler productCreateCommandHandler;
//
//    @DynamicPropertySource
//    static void propertySource(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
//        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
//        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
//        registry.add("spring.datasource.driver-class-name", postgreSQLContainer::getDriverClassName);
//    }
//
//    @Test
//    void repositoryFalls_thenRetryFallbackAreTriggered() {
//        //Arrange
//        UUID categoryId = UUID.randomUUID();
//        UUID userId = UUID.randomUUID();
//
//        ProductCreateCommand productCreateCommand = new ProductCreateCommand(
//                "testTitle", userId, BigDecimal.TEN, 42,
//                "testDesc", "img", categoryId
//        );
//
//        Mockito.doThrow(new TransientDataAccessResourceException("DB down"))
//                .when(productRepository).save(any());
//
//        //Act
//        assertThrows(Exception.class, () -> productCreateCommandHandler.handle(productCreateCommand));
//        //Assert
//        verify(productCreateCommandHandler, atLeast(1))
//                .productCreateRetryFallback(eq(productCreateCommand), any(Throwable.class));
//        verify(productRepository, times(3)).save(any());
//    }
//}
