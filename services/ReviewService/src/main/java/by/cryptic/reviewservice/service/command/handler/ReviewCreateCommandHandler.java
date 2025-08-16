package by.cryptic.reviewservice.service.command.handler;

import by.cryptic.exceptions.CreatingException;
import by.cryptic.reviewservice.mapper.ReviewMapper;
import by.cryptic.reviewservice.model.write.Review;
import by.cryptic.reviewservice.repository.write.ReviewRepository;
import by.cryptic.reviewservice.service.command.ReviewCreateCommand;
import by.cryptic.utils.CommandHandler;
import by.cryptic.utils.event.review.ReviewCreatedEvent;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewCreateCommandHandler implements CommandHandler<ReviewCreateCommand> {

    private final ApplicationEventPublisher eventPublisher;
    private final CacheManager cacheManager;
    private final ReviewRepository reviewRepository;

    @Override
    @Transactional
    @Retry(name = "reviewRetry", fallbackMethod = "reviewRetryFallback")
    public void handle(ReviewCreateCommand dto) {
        log.debug("Trying to create review: {}", dto);
        Review review = Review.builder()
                .title(dto.title())
                .rating(dto.rating())
                .description(dto.description())
                .image(dto.image())
                .productId(dto.productId())
                .userId(dto.userId())
                .build();
        reviewRepository.save(review);
        eventPublisher.publishEvent(ReviewCreatedEvent.builder()
                .reviewId(review.getId())
                .productId(dto.productId())
                .createdBy(review.getCreatedBy())
                .title(review.getTitle())
                .description(review.getDescription())
                .image(review.getImage())
                .build());
        Objects.requireNonNull(cacheManager.getCache("reviews"))
                .put("review:" + review.getId(), ReviewMapper.toDto(review));
    }

    public void reviewRetryFallback(ReviewCreateCommand reviewCreateCommand, Throwable t) {
        log.error("Failed to create {} after all retry attempts. Cause: {}", reviewCreateCommand.title(), t.getMessage(), t);
        throw new CreatingException("Failed to create review:" + reviewCreateCommand.title(), t);
    }
}
