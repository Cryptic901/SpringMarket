package by.cryptic.reviewservice.service.command.handler;

import by.cryptic.exceptions.DeletingException;
import by.cryptic.reviewservice.model.write.Review;
import by.cryptic.reviewservice.repository.write.ReviewRepository;
import by.cryptic.reviewservice.service.command.ReviewDeleteCommand;
import by.cryptic.utils.CommandHandler;
import by.cryptic.utils.event.review.ReviewDeletedEvent;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewDeleteCommandHandler implements CommandHandler<ReviewDeleteCommand> {

    private final ReviewRepository reviewRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    @CacheEvict(cacheNames = "reviews", key = "'review:' + #command.reviewId()")
    @Retry(name = "reviewRetry", fallbackMethod = "reviewRetryDeleteFallback")
    public void handle(ReviewDeleteCommand command) {
        Review review = reviewRepository.findById(command.reviewId())
                .orElseThrow(() -> new EntityNotFoundException
                        ("Review not found with id: %s".formatted(command.reviewId())));
        if (!review.getUserId().equals(command.userId())) {
            throw new IllegalCallerException("It's not your review");
        }
        reviewRepository.deleteById(command.reviewId());
        eventPublisher.publishEvent(new ReviewDeletedEvent(command.reviewId()));
    }

    public void reviewRetryDeleteFallback(ReviewDeleteCommand reviewDeleteCommand, Throwable t) {
        log.error("Failed to delete {} after all retry attempts. Cause: {}", reviewDeleteCommand.reviewId(), t.getMessage(), t);
        throw new DeletingException("Failed to delete review:" + reviewDeleteCommand.reviewId(), t);
    }
}
