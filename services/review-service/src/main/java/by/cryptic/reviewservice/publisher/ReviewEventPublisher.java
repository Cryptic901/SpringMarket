package by.cryptic.reviewservice.publisher;

import by.cryptic.reviewservice.model.write.OutboxEntity;
import by.cryptic.reviewservice.model.write.RatingOnlyReview;
import by.cryptic.reviewservice.model.write.Review;
import by.cryptic.reviewservice.repository.write.OutboxRepository;
import by.cryptic.reviewservice.service.command.RatingOnlyReviewDeleteCommand;
import by.cryptic.reviewservice.service.command.RatingOnlyReviewUpdateCommand;
import by.cryptic.reviewservice.service.command.ReviewDeleteCommand;
import by.cryptic.reviewservice.service.command.ReviewUpdateCommand;
import by.cryptic.utils.event.EventType;
import by.cryptic.utils.event.review.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewEventPublisher {

    private final OutboxRepository outboxRepository;

    public void saveReviewView(Review review) {
        OutboxEntity outboxEntity = OutboxEntity.builder()
                .aggregateId(review.getId())
                .aggregateType("review")
                .eventType(String.valueOf(EventType.ReviewCreatedEvent))
                .payload(ReviewCreatedEvent.builder()
                        .reviewId(review.getId())
                        .productId(review.getProductId())
                        .createdBy(review.getCreatedBy())
                        .rating(review.getRating())
                        .title(review.getTitle())
                        .description(review.getDescription())
                        .image(review.getImage())
                        .build())
                .build();
        outboxRepository.save(outboxEntity);
    }

    public void saveRatingOnlyReviewView(RatingOnlyReview review) {
        OutboxEntity outboxEntity = OutboxEntity.builder()
                .aggregateId(review.getId())
                .aggregateType("review")
                .eventType(String.valueOf(EventType.RatingOnlyReviewCreatedEvent))
                .payload(RatingOnlyReviewCreatedEvent.builder()
                        .reviewId(review.getId())
                        .productId(review.getProductId())
                        .createdBy(review.getCreatedBy())
                        .rating(review.getRating())
                        .build())
                .build();
        outboxRepository.save(outboxEntity);
    }

    public void deleteReviewView(ReviewDeleteCommand command) {
        OutboxEntity outboxEntity = OutboxEntity.builder()
                .aggregateId(command.reviewId())
                .aggregateType("review")
                .eventType(String.valueOf(EventType.ReviewDeletedEvent))
                .payload(ReviewDeletedEvent.builder()
                        .reviewId(command.reviewId())
                        .build())
                .build();
        outboxRepository.save(outboxEntity);
    }

    public void deleteRatingOnlyReview(RatingOnlyReviewDeleteCommand command) {
        OutboxEntity outboxEntity = OutboxEntity.builder()
                .aggregateId(command.reviewId())
                .aggregateType("review")
                .eventType(String.valueOf(EventType.RatingOnlyReviewDeletedEvent))
                .payload(RatingOnlyReviewDeletedEvent.builder()
                        .reviewId(command.reviewId())
                        .build())
                .build();
        outboxRepository.save(outboxEntity);
    }

    public void updateReviewView(Review review, ReviewUpdateCommand dto) {
        OutboxEntity outboxEntity = OutboxEntity.builder()
                .aggregateId(review.getId())
                .aggregateType("review")
                .eventType(String.valueOf(EventType.ReviewUpdatedEvent))
                .payload(ReviewUpdatedEvent.builder()
                        .title(review.getTitle())
                        .image(review.getImage())
                        .rating(review.getRating())
                        .updatedAt(review.getUpdatedAt())
                        .reviewId(dto.reviewId())
                        .productId(review.getProductId())
                        .description(review.getDescription())
                        .updatedBy(review.getUpdatedBy())
                        .build())
                .build();
        outboxRepository.save(outboxEntity);
    }

    public void updateRatingOnlyReviewView(RatingOnlyReview review,
                                           RatingOnlyReviewUpdateCommand dto) {
        OutboxEntity outboxEntity = OutboxEntity.builder()
                .aggregateId(review.getId())
                .aggregateType("review")
                .eventType(String.valueOf(EventType.RatingOnlyReviewUpdatedEvent))
                .payload(RatingOnlyReviewUpdatedEvent.builder()
                        .rating(review.getRating())
                        .reviewId(dto.reviewId())
                        .productId(review.getProductId())
                        .build())
                .build();
        outboxRepository.save(outboxEntity);
    }
}
