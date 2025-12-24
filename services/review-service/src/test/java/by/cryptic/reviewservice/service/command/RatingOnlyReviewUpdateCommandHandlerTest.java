package by.cryptic.reviewservice.service.command;

import by.cryptic.reviewservice.model.write.RatingOnlyReview;
import by.cryptic.reviewservice.publisher.ReviewEventPublisher;
import by.cryptic.reviewservice.repository.write.RatingOnlyReviewRepository;
import by.cryptic.reviewservice.service.command.handler.RatingOnlyReviewUpdateCommandHandler;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class RatingOnlyReviewUpdateCommandHandlerTest {

    @Mock
    private RatingOnlyReviewRepository reviewRepository;

    @Mock
    private ReviewEventPublisher reviewEventPublisher;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private RatingOnlyReviewUpdateCommandHandler reviewUpdateCommandHandler;

    @Test
    void updateReview_whenReviewIsExists_shouldDeleteReview() {
        //Arrange
        UUID reviewId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        RatingOnlyReview review = RatingOnlyReview.builder()
                .id(reviewId)
                .productId(productId)
                .rating(3.4)
                .createdAt(LocalDateTime.now())
                .createdBy(userId)
                .build();
        RatingOnlyReviewUpdateCommand reviewUpdateCommand = new RatingOnlyReviewUpdateCommand(reviewId,
                3.2, userId);
        Mockito.when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        Mockito.when(cacheManager.getCache("reviews")).thenReturn(cache);
        //Act
        reviewUpdateCommandHandler.handle(reviewUpdateCommand);
        //Assert
        Mockito.verify(reviewRepository, Mockito.times(1)).findById(reviewId);
        Mockito.verify(reviewRepository, Mockito.times(1)).save(any(RatingOnlyReview.class));
        Mockito.verifyNoMoreInteractions(reviewRepository);
    }

    @Test
    void updateReview_whenReviewIsNotExists_shouldThrowEntityNotFoundException() {
        //Arrange
        UUID reviewId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        RatingOnlyReviewUpdateCommand reviewUpdateCommand = new RatingOnlyReviewUpdateCommand(reviewId,
                3.2, userId);
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
        RatingOnlyReview review = RatingOnlyReview.builder()
                .id(reviewId)
                .productId(productId)
                .rating(3.4)
                .createdAt(LocalDateTime.now())
                .createdBy(userId)
                .build();
        RatingOnlyReviewUpdateCommand reviewUpdateCommand = new RatingOnlyReviewUpdateCommand(reviewId,
                3.2, userId2);
        Mockito.when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        //Act
        //Assert
        assertThrows(IllegalCallerException.class, () -> reviewUpdateCommandHandler.handle(reviewUpdateCommand));
    }
}