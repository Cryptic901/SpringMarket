package by.cryptic.reviewservice.service.command;

import by.cryptic.reviewservice.model.write.Review;
import by.cryptic.reviewservice.repository.write.ReviewRepository;
import by.cryptic.reviewservice.service.command.handler.ReviewDeleteCommandHandler;
import by.cryptic.utils.event.review.ReviewDeletedEvent;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ReviewDeleteCommandHandlerTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private ReviewDeleteCommandHandler reviewDeleteCommandHandler;

    @Test
    void deleteReview_whenReviewIsExists_shouldDeleteReview() {
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
        ReviewDeleteCommand reviewDeleteCommand = new ReviewDeleteCommand(reviewId, userId);
        Mockito.when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        Mockito.doNothing().when(reviewRepository).deleteById(reviewId);
        //Act
        reviewDeleteCommandHandler.handle(reviewDeleteCommand);
        //Assert
        Mockito.verify(reviewRepository, Mockito.times(1)).findById(reviewId);
        Mockito.verify(reviewRepository, Mockito.times(1)).deleteById(reviewId);
        Mockito.verify(applicationEventPublisher, Mockito.times(1)).publishEvent(any(ReviewDeletedEvent.class));
        Mockito.verifyNoMoreInteractions(reviewRepository, applicationEventPublisher);
    }

    @Test
    void deleteReview_whenReviewIsNotExists_shouldThrowEntityNotFoundException() {
        //Arrange
        UUID reviewId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        ReviewDeleteCommand reviewDeleteCommand = new ReviewDeleteCommand(reviewId, userId);
        //Act
        //Assert
        assertThrows(EntityNotFoundException.class, () -> reviewDeleteCommandHandler.handle(reviewDeleteCommand));
    }

    @Test
    void deleteReview_whenUserIsNotReviewCreator_shouldThrowIllegalCallerException() {
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
        ReviewDeleteCommand reviewDeleteCommand = new ReviewDeleteCommand(reviewId, userId2);
        Mockito.when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        //Act
        //Assert
        assertThrows(IllegalCallerException.class, () -> reviewDeleteCommandHandler.handle(reviewDeleteCommand));
    }
}