package by.cryptic.reviewservice.service.command.handler;

import by.cryptic.reviewservice.mapper.ReviewMapper;
import by.cryptic.reviewservice.model.write.Review;
import by.cryptic.reviewservice.repository.write.ReviewRepository;
import by.cryptic.reviewservice.service.command.ReviewUpdateCommand;
import by.cryptic.utils.CommandHandler;
import by.cryptic.utils.event.review.ReviewUpdatedEvent;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@CacheConfig(cacheNames = {"reviews"})
public class ReviewUpdateCommandHandler implements CommandHandler<ReviewUpdateCommand> {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final CacheManager cacheManager;

    @Transactional
    @Retry(name = "reviewRetry", fallbackMethod = "reviewUpdateFallback")
    @Bulkhead(name = "reviewBulkhead", fallbackMethod = "reviewUpdateFallback")
    public void handle(ReviewUpdateCommand dto) throws AccessDeniedException {
        Review review = reviewRepository.findById(dto.reviewId())
                .orElseThrow(() -> new EntityNotFoundException
                        ("Review not found with id: %s".formatted(dto.reviewId())));
        if (!review.getUserId().equals(dto.userId())) {
            throw new AccessDeniedException("You are not allowed to update this review");
        }
        reviewMapper.updateEntity(review, dto);
        eventPublisher.publishEvent(ReviewUpdatedEvent.builder()
                .title(review.getTitle())
                .image(review.getImage())
                .rating(review.getRating())
                .reviewId(dto.reviewId())
                .productId(review.getProductId())
                .description(review.getDescription())
                .updatedBy(review.getUpdatedBy())
                .build());
        Objects.requireNonNull(cacheManager.getCache("reviews"))
                .put("review:" + dto.reviewId(), review);
    }

    public void reviewUpdateFallback(ReviewUpdateCommand reviewUpdateCommand, Throwable t) {
        log.error("Failed to update {}, {}", reviewUpdateCommand.reviewId(), t);
        throw new RuntimeException(t.getMessage());
    }
}
