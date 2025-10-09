package by.cryptic.reviewservice.service.command.handler;

import by.cryptic.exceptions.CreatingException;
import by.cryptic.reviewservice.client.ProductServiceClient;
import by.cryptic.reviewservice.mapper.ReviewMapper;
import by.cryptic.reviewservice.model.write.RatingOnlyReview;
import by.cryptic.reviewservice.publisher.ReviewEventPublisher;
import by.cryptic.reviewservice.repository.write.RatingOnlyReviewRepository;
import by.cryptic.reviewservice.service.command.RatingOnlyReviewCreateCommand;
import by.cryptic.utils.DTO.ProductDTO;
import by.cryptic.utils.handler.CommandHandler;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingOnlyReviewCreateCommandHandler implements CommandHandler<RatingOnlyReviewCreateCommand> {

    private final CacheManager cacheManager;
    private final RatingOnlyReviewRepository ratingOnlyReviewRepository;
    private final ReviewEventPublisher reviewEventPublisher;
    private final ProductServiceClient productServiceClient;

    @Override
    @Transactional
    @Retry(name = "reviewRetry", fallbackMethod = "reviewRetryFallback")
    public void handle(RatingOnlyReviewCreateCommand dto) {
        log.info("Trying to create rating only review: {}", dto);
        RatingOnlyReview ratingOnlyReview = saveReview(dto);

        reviewEventPublisher.saveRatingOnlyReviewView(ratingOnlyReview);

        updateCache(ratingOnlyReview);
    }

    private void updateCache(RatingOnlyReview ratingOnlyReview) {
        try {
            Objects.requireNonNull(cacheManager.getCache("reviews"))
                    .put("review:" + ratingOnlyReview.getId(), ReviewMapper.toDto(ratingOnlyReview));
        } catch (Exception e) {
            log.warn("Failed to update review cache", e);
        }
    }

    public RatingOnlyReview saveReview(RatingOnlyReviewCreateCommand dto) {
        if (getProductByFeignClient(dto.productId()) == null) {
            throw new EntityNotFoundException("Product not found with id");
        }
        RatingOnlyReview ratingOnlyReview = RatingOnlyReview.builder()
                .rating(dto.rating())
                .productId(dto.productId())
                .createdBy(dto.userId())
                .build();
        return ratingOnlyReviewRepository.save(ratingOnlyReview);
    }

    @CircuitBreaker(name = "productCircuitBreaker", fallbackMethod = "productClientCircuitBreakerFallback")
    public ProductDTO getProductByFeignClient(UUID productId) {
        return productServiceClient.getProductById(productId).getBody();
    }

    public void reviewRetryFallback(RatingOnlyReviewCreateCommand dto, Throwable t) {
        log.error("Failed to create review for {} after all retry attempts. Cause: {}", dto.productId(), t.getMessage(), t);
        throw new CreatingException("Failed to create review for product:" + dto.productId(), t);
    }

    public ProductDTO productClientCircuitBreakerFallback(UUID productId, Throwable t) {
        log.error("Failed to create review {} after all retry attempts. Cause: {}", productId, t.getMessage(), t);
        throw new CreatingException("Failed to create review with productId:" + productId, t);
    }
}
