package by.cryptic.reviewservice.service.command.handler;

import by.cryptic.exceptions.UpdatingException;
import by.cryptic.reviewservice.mapper.ReviewMapper;
import by.cryptic.reviewservice.model.write.Review;
import by.cryptic.reviewservice.repository.write.ReviewRepository;
import by.cryptic.reviewservice.service.command.ReviewUpdateCommand;
import by.cryptic.utils.CommandHandler;
import by.cryptic.utils.event.review.ReviewUpdatedEvent;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewUpdateCommandHandler implements CommandHandler<ReviewUpdateCommand> {

    private final ReviewRepository reviewRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    @Retry(name = "reviewRetry", fallbackMethod = "reviewRetryUpdateFallback")
    @CachePut(cacheNames = "reviews", key = "'review:' + dto.reviewId()")
    public void handle(ReviewUpdateCommand dto) throws IllegalCallerException {
        Review review = reviewRepository.findById(dto.reviewId())
                .orElseThrow(() -> new EntityNotFoundException
                        ("Review not found with id: %s".formatted(dto.reviewId())));
        if (!review.getUserId().equals(dto.userId())) {
            throw new IllegalCallerException("You are not allowed to update this review");
        }
        ReviewMapper.updateEntity(review, dto);
        reviewRepository.save(review);
        eventPublisher.publishEvent(ReviewUpdatedEvent.builder()
                .title(review.getTitle())
                .image(review.getImage())
                .rating(review.getRating())
                .reviewId(dto.reviewId())
                .productId(review.getProductId())
                .description(review.getDescription())
                .updatedBy(review.getUpdatedBy())
                .build());
    }

    public void reviewRetryUpdateFallback(ReviewUpdateCommand reviewUpdateCommand, Throwable t) {
        log.error("Failed to update {} after all retry attempts. Cause: {}", reviewUpdateCommand.title(), t.getMessage(), t);
        throw new UpdatingException("Failed to update review:" + reviewUpdateCommand.title(), t);
    }
}
