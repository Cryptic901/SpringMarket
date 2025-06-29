package by.cryptic.reviewservice.service.command.handler;

import by.cryptic.utils.event.review.ReviewUpdatedEvent;
import by.cryptic.reviewservice.mapper.ReviewMapper;
import by.cryptic.reviewservice.model.write.Review;
import by.cryptic.reviewservice.repository.write.ReviewRepository;
import by.cryptic.reviewservice.service.command.ReviewUpdateCommand;
import by.cryptic.utils.CommandHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"reviews"})
public class ReviewUpdateCommandHandler implements CommandHandler<ReviewUpdateCommand> {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final CacheManager cacheManager;

    @Transactional
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
}
