package by.cryptic.reviewservice.publisher;

import by.cryptic.exceptions.CreatingException;
import by.cryptic.exceptions.DeletingException;
import by.cryptic.exceptions.UpdatingException;
import by.cryptic.reviewservice.model.write.OutboxEntity;
import by.cryptic.reviewservice.model.write.Review;
import by.cryptic.reviewservice.repository.write.OutboxRepository;
import by.cryptic.reviewservice.service.command.ReviewDeleteCommand;
import by.cryptic.reviewservice.service.command.ReviewUpdateCommand;
import by.cryptic.utils.event.EventType;
import by.cryptic.utils.event.review.ReviewCreatedEvent;
import by.cryptic.utils.event.review.ReviewDeletedEvent;
import by.cryptic.utils.event.review.ReviewUpdatedEvent;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewEventPublisher {

    private final OutboxRepository outboxRepository;

    @Retry(name = "reviewRetry", fallbackMethod = "reviewRetryFallback")
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

    @Retry(name = "reviewRetry", fallbackMethod = "reviewRetryDeleteFallback")
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

    @Retry(name = "reviewRetry", fallbackMethod = "reviewRetryUpdateFallback")
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

    public void reviewRetryDeleteFallback(ReviewDeleteCommand reviewDeleteCommand, Throwable t) {
        log.error("Failed to delete {} after all retry attempts. Cause: {}", reviewDeleteCommand.reviewId(), t.getMessage(), t);
        throw new DeletingException("Failed to delete review:" + reviewDeleteCommand.reviewId(), t);
    }

    public void reviewRetryFallback(Review review, Throwable t) {
        log.error("Failed to create {} after all retry attempts. Cause: {}", review.getTitle(), t.getMessage(), t);
        throw new CreatingException("Failed to create review:" + review.getTitle(), t);
    }

    public void reviewRetryUpdateFallback(Review review, ReviewUpdateCommand dto, Throwable t) {
        log.error("Failed to update {} after all retry attempts. Cause: {}", dto.title(), t.getMessage(), t);
        throw new UpdatingException("Failed to update review:" + dto.title(), t);
    }
}
