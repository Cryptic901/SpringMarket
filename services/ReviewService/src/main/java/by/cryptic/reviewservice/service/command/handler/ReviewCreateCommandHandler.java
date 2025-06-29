package by.cryptic.reviewservice.service.command.handler;

import by.cryptic.utils.event.review.ReviewCreatedEvent;
import by.cryptic.reviewservice.model.write.Review;
import by.cryptic.reviewservice.service.command.ReviewCreateCommand;
import by.cryptic.utils.CommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"reviews"})
public class ReviewCreateCommandHandler implements CommandHandler<ReviewCreateCommand> {

    private final ApplicationEventPublisher eventPublisher;
    private final CacheManager cacheManager;

    @Transactional
    public void handle(ReviewCreateCommand dto) {
        Review review = Review.builder()
                .title(dto.title())
                .rating(dto.rating())
                .description(dto.description())
                .image(dto.image())
                .productId(dto.productId())
                .build();
//        user.getReviews().add(review);
        eventPublisher.publishEvent(ReviewCreatedEvent.builder()
                .reviewId(review.getId())
                .productId(dto.productId())
                .createdBy(review.getCreatedBy())
                .title(review.getTitle())
                .description(review.getDescription())
                .image(review.getImage())
                .build());
        Objects.requireNonNull(cacheManager.getCache("reviews"))
                .put("review:" + dto.title() + '-' + dto.description(), review);
    }
}
