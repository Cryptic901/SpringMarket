package by.cryptic.reviewservice.service.command.it.retry;

import by.cryptic.reviewservice.model.write.Review;
import by.cryptic.reviewservice.repository.write.ReviewRepository;
import by.cryptic.reviewservice.service.command.ReviewUpdateCommand;
import by.cryptic.reviewservice.service.command.handler.ReviewUpdateCommandHandler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/* Run Docker Desktop before start */
@SpringBootTest
@ActiveProfiles(value = {"test", "jpa"})
@Testcontainers
class ReviewUpdateCommandHandlerRetryTest {

    @MockitoBean
    private ReviewRepository reviewRepository;

    @Container
    static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    @MockitoSpyBean
    private ReviewUpdateCommandHandler reviewUpdateCommandHandler;

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
        ReviewUpdateCommand reviewUpdateCommand = new ReviewUpdateCommand(reviewId,
                "newTitle", null, null, null, userId);

        Mockito.when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        Mockito.doThrow(new TransientDataAccessResourceException("DB down"))
                .when(reviewRepository).save(review);
        //Act
        assertThrows(Exception.class, () -> reviewUpdateCommandHandler.handle(reviewUpdateCommand));
        //Assert
        verify(reviewUpdateCommandHandler, atLeast(1))
                .reviewRetryUpdateFallback(eq(reviewUpdateCommand), any(Throwable.class));
        verify(reviewRepository, times(3)).findById(any());
    }
}
