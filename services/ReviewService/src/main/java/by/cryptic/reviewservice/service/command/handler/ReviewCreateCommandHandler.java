package by.cryptic.reviewservice.service.command.handler;

import by.cryptic.reviewservice.mapper.ReviewMapper;
import by.cryptic.reviewservice.repository.write.ReviewRepository;
import by.cryptic.utils.event.review.ReviewCreatedEvent;
import by.cryptic.reviewservice.model.write.Review;
import by.cryptic.reviewservice.service.command.ReviewCreateCommand;
import by.cryptic.utils.CommandHandler;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
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
@CacheConfig(cacheNames = {"reviews"})
public class ReviewCreateCommandHandler implements CommandHandler<ReviewCreateCommand> {

    private final ApplicationEventPublisher eventPublisher;
    private final CacheManager cacheManager;
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    @Transactional
    @Retry(name = "reviewRetry", fallbackMethod = "reviewCreateFallback")
    @Bulkhead(name = "reviewBulkhead", fallbackMethod = "reviewCreateFallback")
    public void handle(ReviewCreateCommand dto) {
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
                .put("review:" + review.getId(), reviewMapper.toDto(review));
    }

    public void reviewCreateFallback(ReviewCreateCommand reviewCreateCommand, Throwable t) {
        log.error("Failed to create {}, {}", reviewCreateCommand.title(), t);
        throw new RuntimeException(t.getMessage());
    }
}
