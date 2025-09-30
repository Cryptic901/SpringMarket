package by.cryptic.reviewservice.service.command;

import by.cryptic.reviewservice.model.write.RatingOnlyReview;
import by.cryptic.reviewservice.publisher.ReviewEventPublisher;
import by.cryptic.reviewservice.repository.write.RatingOnlyReviewRepository;
import by.cryptic.reviewservice.service.command.handler.RatingOnlyReviewDeleteCommandHandler;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class RatingOnlyReviewDeleteCommandHandlerTest {

    @Mock
    private RatingOnlyReviewRepository reviewRepository;

    @Mock
    private ReviewEventPublisher reviewEventPublisher;

    @InjectMocks
    private RatingOnlyReviewDeleteCommandHandler reviewDeleteCommandHandler;

    @Test
    void deleteReview_whenReviewIsExists_shouldDeleteReview() {
        //Arrange
        UUID reviewId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        RatingOnlyReview review = RatingOnlyReview.builder()
                .id(reviewId)
                .createdBy(userId)
                .productId(productId)
                .rating(3.4)
                .createdAt(LocalDateTime.now())
                .build();
        RatingOnlyReviewDeleteCommand reviewDeleteCommand = new RatingOnlyReviewDeleteCommand(reviewId, userId);
        Mockito.when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        Mockito.doNothing().when(reviewRepository).deleteById(reviewId);
        //Act
        reviewDeleteCommandHandler.handle(reviewDeleteCommand);
        //Assert
        Mockito.verify(reviewRepository, Mockito.times(1)).findById(reviewId);
        Mockito.verify(reviewRepository, Mockito.times(1)).deleteById(reviewId);
    }

    @Test
    void deleteReview_whenReviewIsNotExists_shouldThrowEntityNotFoundException() {
        //Arrange
        UUID reviewId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        RatingOnlyReviewDeleteCommand reviewDeleteCommand = new RatingOnlyReviewDeleteCommand(reviewId, userId);
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
        RatingOnlyReview review = RatingOnlyReview.builder()
                .id(reviewId)
                .createdBy(userId)
                .productId(productId)
                .rating(3.4)
                .createdAt(LocalDateTime.now())
                .build();
        RatingOnlyReviewDeleteCommand reviewDeleteCommand = new RatingOnlyReviewDeleteCommand(reviewId, userId2);
        Mockito.when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        //Act
        //Assert
        assertThrows(IllegalCallerException.class, () -> reviewDeleteCommandHandler.handle(reviewDeleteCommand));
    }
}