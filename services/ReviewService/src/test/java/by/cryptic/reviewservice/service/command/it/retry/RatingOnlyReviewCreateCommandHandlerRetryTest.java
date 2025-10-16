package by.cryptic.reviewservice.service.command.it.retry;

import by.cryptic.exceptions.CreatingException;
import by.cryptic.reviewservice.ReviewServiceApplication;
import by.cryptic.reviewservice.client.ProductServiceClient;
import by.cryptic.reviewservice.repository.write.RatingOnlyReviewRepository;
import by.cryptic.reviewservice.service.command.RatingOnlyReviewCreateCommand;
import by.cryptic.reviewservice.service.command.handler.RatingOnlyReviewCreateCommandHandler;
import by.cryptic.utils.DTO.ProductDTO;
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
        classes = ReviewServiceApplication.class,
        properties = "spring.kafka.listener.auto-startup=false"
)
@ImportAutoConfiguration(exclude = KafkaAutoConfiguration.class)
@ActiveProfiles(value = {"test", "jpa"})
@Testcontainers
class RatingOnlyReviewCreateCommandHandlerRetryTest {

    @MockitoBean
    private RatingOnlyReviewRepository reviewRepository;

    @Container
    static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    @MockitoSpyBean
    private RatingOnlyReviewCreateCommandHandler reviewCreateCommandHandler;

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
        UUID productId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        RatingOnlyReviewCreateCommand reviewCreateCommand = new RatingOnlyReviewCreateCommand(3.4,
                productId, userId
        );
        ProductDTO productDTO = new ProductDTO("name", BigDecimal.ONE, 42, "desc",
                "img/url", UUID.randomUUID());

        Mockito.when(productServiceClient.getProductById(productId)).thenReturn(ResponseEntity.ok(productDTO));
        Mockito.doThrow(new TransientDataAccessResourceException("DB down"))
                .when(reviewRepository).save(any());

        //Act
        assertThrows(CreatingException.class, () -> reviewCreateCommandHandler.handle(reviewCreateCommand));
        //Assert
        verify(reviewCreateCommandHandler, atLeast(1))
                .reviewRetryFallback(eq(reviewCreateCommand), any(Throwable.class));
        verify(reviewRepository, times(3)).save(any());
    }
}

