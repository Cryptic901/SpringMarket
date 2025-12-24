package by.cryptic.reviewservice.service.command;

import by.cryptic.reviewservice.model.write.Review;
import by.cryptic.reviewservice.publisher.ReviewEventPublisher;
import by.cryptic.reviewservice.repository.write.ReviewRepository;
import by.cryptic.reviewservice.service.command.handler.ReviewUpdateCommandHandler;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ReviewUpdateCommandHandlerTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewEventPublisher reviewEventPublisher;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private ReviewUpdateCommandHandler reviewUpdateCommandHandler;

    @Test
    void updateReview_whenReviewIsExists_shouldDeleteReview() {
        //Arrange
        UUID reviewId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
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
        Mockito.when(cacheManager.getCache("reviews")).thenReturn(cache);
        //Act
        reviewUpdateCommandHandler.handle(reviewUpdateCommand);
        //Assert
        Mockito.verify(reviewRepository, Mockito.times(1)).findById(reviewId);
        Mockito.verify(reviewRepository, Mockito.times(1)).save(any(Review.class));
        Mockito.verifyNoMoreInteractions(reviewRepository);
    }

    @Test
    void updateReview_whenReviewIsNotExists_shouldThrowEntityNotFoundException() {
        //Arrange
        UUID reviewId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        ReviewUpdateCommand reviewUpdateCommand = new ReviewUpdateCommand(reviewId,
                "newTitle", null, null, null, userId);
        //Act
        //Assert
        assertThrows(EntityNotFoundException.class, () -> reviewUpdateCommandHandler.handle(reviewUpdateCommand));
    }

    @Test
    void updateReview_whenUserIsNotReviewCreator_shouldThrowIllegalCallerException() {
        //Arrange
        UUID reviewId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
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
                "newTitle", null, null, null, userId2);
        Mockito.when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        //Act
        //Assert
        assertThrows(IllegalCallerException.class, () -> reviewUpdateCommandHandler.handle(reviewUpdateCommand));
    }
}