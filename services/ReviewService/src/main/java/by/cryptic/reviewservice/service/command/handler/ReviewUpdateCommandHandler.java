package by.cryptic.reviewservice.service.command.handler;

import by.cryptic.exceptions.UpdatingException;
import by.cryptic.reviewservice.mapper.ReviewMapper;
import by.cryptic.reviewservice.model.write.Review;
import by.cryptic.reviewservice.publisher.ReviewEventPublisher;
import by.cryptic.reviewservice.repository.write.ReviewRepository;
import by.cryptic.reviewservice.service.command.ReviewUpdateCommand;
import by.cryptic.utils.handler.CommandHandler;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewUpdateCommandHandler implements CommandHandler<ReviewUpdateCommand> {

    private final ReviewRepository reviewRepository;
    private final ReviewEventPublisher reviewEventPublisher;
    private final CacheManager cacheManager;

    @Override
    @Transactional
    @Retry(name = "reviewRetry", fallbackMethod = "reviewRetryUpdateFallback")
    public void handle(ReviewUpdateCommand dto) {
        Review review = getReviewAndValidateAccess(dto);

        updateReview(review, dto);

        reviewEventPublisher.updateReviewView(review, dto);

        updateCache(review);
    }

    private Review getReviewAndValidateAccess(ReviewUpdateCommand dto) {
        Review review = reviewRepository.findById(dto.reviewId())
                .orElseThrow(() -> new EntityNotFoundException
                        ("Review not found with id: %s".formatted(dto.reviewId())));
        if (!review.getUserId().equals(dto.userId())) {
            throw new IllegalCallerException("You are not allowed to update this review");
        }
        return review;
    }

    private void updateCache(Review review) {
        Objects.requireNonNull(cacheManager.getCache("reviews"))
                .put("review:" + review.getId(), review);
    }

    public void updateReview(Review review, ReviewUpdateCommand dto) {
        ReviewMapper.updateEntity(review, dto);
        reviewRepository.save(review);
    }

    public void reviewRetryUpdateFallback(ReviewUpdateCommand dto, Throwable t) {
        log.error("Failed to update {} after all retry attempts. Cause: {}", dto.title(), t.getMessage(), t);
        throw new UpdatingException("Failed to update review:" + dto.title(), t);
    }
}
