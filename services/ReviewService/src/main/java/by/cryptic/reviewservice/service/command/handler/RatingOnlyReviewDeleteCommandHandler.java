package by.cryptic.reviewservice.service.command.handler;

import by.cryptic.exceptions.DeletingException;
import by.cryptic.reviewservice.model.write.RatingOnlyReview;
import by.cryptic.reviewservice.publisher.ReviewEventPublisher;
import by.cryptic.reviewservice.repository.write.RatingOnlyReviewRepository;
import by.cryptic.reviewservice.service.command.RatingOnlyReviewDeleteCommand;
import by.cryptic.utils.handler.CommandHandler;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingOnlyReviewDeleteCommandHandler implements CommandHandler<RatingOnlyReviewDeleteCommand> {

    private final RatingOnlyReviewRepository reviewRepository;
    private final ReviewEventPublisher reviewEventPublisher;

    @Override
    @Transactional
    @CacheEvict(cacheNames = "reviews", key = "'review:' + #command.reviewId()")
    public void handle(RatingOnlyReviewDeleteCommand command) {
        getReviewAndValidateAccess(command);

        deleteReview(command);

        reviewEventPublisher.deleteRatingOnlyReview(command);
    }

    private void getReviewAndValidateAccess(RatingOnlyReviewDeleteCommand command) {
        RatingOnlyReview review = reviewRepository.findById(command.reviewId())
                .orElseThrow(() -> new EntityNotFoundException
                        ("Review not found with id: %s".formatted(command.reviewId())));
        if (!review.getCreatedBy().equals(command.userId())) {
            throw new IllegalCallerException("It's not your review");
        }
    }

    @Retry(name = "reviewRetry", fallbackMethod = "reviewRetryDeleteFallback")
    public void deleteReview(RatingOnlyReviewDeleteCommand command) {
        reviewRepository.deleteById(command.reviewId());
    }

    public void reviewRetryDeleteFallback(RatingOnlyReviewDeleteCommand command, Throwable t) {
        log.error("Failed to delete {} after all retry attempts. Cause: {}", command.reviewId(), t.getMessage(), t);
        throw new DeletingException("Failed to delete review:" + command.reviewId(), t);
    }
}
