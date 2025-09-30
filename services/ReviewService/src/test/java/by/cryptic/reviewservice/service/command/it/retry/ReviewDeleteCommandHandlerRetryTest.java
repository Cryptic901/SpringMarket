package by.cryptic.reviewservice.service.command.it.retry;

import by.cryptic.exceptions.DeletingException;
import by.cryptic.reviewservice.ReviewServiceApplication;
import by.cryptic.reviewservice.client.ProductServiceClient;
import by.cryptic.reviewservice.model.write.Review;
import by.cryptic.reviewservice.repository.write.ReviewRepository;
import by.cryptic.reviewservice.service.command.ReviewDeleteCommand;
import by.cryptic.reviewservice.service.command.handler.ReviewDeleteCommandHandler;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/* Run Docker Desktop before start */
@SpringBootTest(
        classes = ReviewServiceApplication.class,
        properties = "spring.kafka.listener.auto-startup=false"
)
@ImportAutoConfiguration(exclude = KafkaAutoConfiguration.class)
@ActiveProfiles(value = {"test", "jpa"})
@Testcontainers
class ReviewDeleteCommandHandlerRetryTest {

    @MockitoBean
    private ReviewRepository reviewRepository;

    @Container
    static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    @MockitoSpyBean
    private ReviewDeleteCommandHandler reviewDeleteCommandHandler;

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
        UUID reviewId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Review review = Review.builder()
                .id(reviewId)
                .userId(userId)
                .productId(productId)
                .title("testReview")
                .description("testDesc")
                .image("test/img")
                .rating(3.4)
                .build();
        ReviewDeleteCommand reviewDeleteCommand = new ReviewDeleteCommand(reviewId, userId);
        ProductDTO productDTO = new ProductDTO("name", BigDecimal.ONE, 42, "desc",
                "img/url", UUID.randomUUID());

        Mockito.when(productServiceClient.getProductById(productId)).thenReturn(ResponseEntity.ok(productDTO));
        Mockito.when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        Mockito.doThrow(new TransientDataAccessResourceException("DB down"))
                .when(reviewRepository).deleteById(reviewId);

        //Act
        assertThrows(DeletingException.class, () -> reviewDeleteCommandHandler.deleteReview(reviewDeleteCommand));
        //Assert
        verify(reviewDeleteCommandHandler, atLeast(1))
                .reviewRetryDeleteFallback(eq(reviewDeleteCommand), any(Throwable.class));
        verify(reviewRepository, times(3)).deleteById(any());
    }
}
