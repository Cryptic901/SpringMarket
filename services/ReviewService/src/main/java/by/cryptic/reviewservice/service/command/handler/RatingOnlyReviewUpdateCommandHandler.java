package by.cryptic.reviewservice.service.command.handler;

import by.cryptic.exceptions.UpdatingException;
import by.cryptic.reviewservice.mapper.ReviewMapper;
import by.cryptic.reviewservice.model.write.RatingOnlyReview;
import by.cryptic.reviewservice.publisher.ReviewEventPublisher;
import by.cryptic.reviewservice.repository.write.RatingOnlyReviewRepository;
import by.cryptic.reviewservice.service.command.RatingOnlyReviewUpdateCommand;
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
public class RatingOnlyReviewUpdateCommandHandler implements CommandHandler<RatingOnlyReviewUpdateCommand> {

    private final RatingOnlyReviewRepository ratingOnlyReviewRepository;
    private final ReviewEventPublisher reviewEventPublisher;
    private final CacheManager cacheManager;

    @Override
    @Transactional
    public void handle(RatingOnlyReviewUpdateCommand dto) {
        RatingOnlyReview review = getReviewAndValidateAccess(dto);

        updateReview(review, dto);

        reviewEventPublisher.updateRatingOnlyReviewView(review, dto);

        updateCache(review);
    }

    private RatingOnlyReview getReviewAndValidateAccess(RatingOnlyReviewUpdateCommand dto) {
        RatingOnlyReview review = ratingOnlyReviewRepository.findById(dto.reviewId())
                .orElseThrow(() -> new EntityNotFoundException
                        ("Review not found with id: %s".formatted(dto.reviewId())));
        if (!review.getCreatedBy().equals(dto.userId())) {
            throw new IllegalCallerException("You are not allowed to update this review");
        }
        return review;
    }

    private void updateCache(RatingOnlyReview review) {
        try {
            Objects.requireNonNull(cacheManager.getCache("reviews"))
                    .put("review:" + review.getId(), review);
        } catch (Exception e) {
            log.warn("Failed to update review cache", e);
        }
    }

    @Retry(name = "reviewRetry", fallbackMethod = "reviewRetryUpdateFallback")
    public void updateReview(RatingOnlyReview review, RatingOnlyReviewUpdateCommand dto) {
        ReviewMapper.updateEntity(review, dto);
        ratingOnlyReviewRepository.save(review);
    }

    public void reviewRetryUpdateFallback(RatingOnlyReview review, ReviewUpdateCommand dto, Throwable t) {
        log.error("Failed to update {} after all retry attempts. Cause: {}", dto.title(), t.getMessage(), t);
        throw new UpdatingException("Failed to update review:" + dto.title(), t);
    }
}
