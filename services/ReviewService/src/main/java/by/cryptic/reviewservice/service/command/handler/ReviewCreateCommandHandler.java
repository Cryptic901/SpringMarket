package by.cryptic.reviewservice.service.command.handler;

import by.cryptic.exceptions.CreatingException;
import by.cryptic.reviewservice.client.ProductServiceClient;
import by.cryptic.reviewservice.mapper.ReviewMapper;
import by.cryptic.reviewservice.model.write.Review;
import by.cryptic.reviewservice.publisher.ReviewEventPublisher;
import by.cryptic.reviewservice.repository.write.ReviewRepository;
import by.cryptic.reviewservice.service.command.ReviewCreateCommand;
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
public class ReviewCreateCommandHandler implements CommandHandler<ReviewCreateCommand> {

    private final CacheManager cacheManager;
    private final ReviewRepository reviewRepository;
    private final ReviewEventPublisher reviewEventPublisher;
    private final ProductServiceClient productServiceClient;

    @Override
    @Transactional
    public void handle(ReviewCreateCommand dto) {
        log.info("Trying to create review: {}", dto);
        Review review = saveReview(dto);

        reviewEventPublisher.saveReviewView(review);

        updateCache(review);
    }

    private void updateCache(Review review) {
        try {
            Objects.requireNonNull(cacheManager.getCache("reviews"))
                    .put("review:" + review.getId(), ReviewMapper.toDto(review));
        } catch (Exception e) {
            log.warn("Failed to update review cache", e);
        }
    }

    @Retry(name = "reviewRetry", fallbackMethod = "reviewRetryFallback")
    public Review saveReview(ReviewCreateCommand dto) {
        if (getProductByFeignClient(dto.productId()) == null) {
            throw new EntityNotFoundException("Product not found with id");
        }
        Review review = Review.builder()
                .title(dto.title())
                .rating(dto.rating())
                .description(dto.description())
                .image(dto.image())
                .productId(dto.productId())
                .userId(dto.userId())
                .build();
        return reviewRepository.save(review);
    }

    @CircuitBreaker(name = "productCircuitBreaker", fallbackMethod = "productClientCircuitBreakerFallback")
    public ProductDTO getProductByFeignClient(UUID productId) {
        return productServiceClient.getProductById(productId).getBody();
    }

    public Review reviewRetryFallback(ReviewCreateCommand dto, Throwable t) {
        log.error("Failed to create {} after all retry attempts. Cause: {}", dto.title(), t.getMessage(), t);
        throw new CreatingException("Failed to create review:" + dto.title(), t);
    }

    public ProductDTO productClientCircuitBreakerFallback(UUID productId, Throwable t) {
        log.error("Failed to create review {} after all retry attempts. Cause: {}", productId, t.getMessage(), t);
        throw new CreatingException("Failed to create review with productId:" + productId, t);
    }
}
