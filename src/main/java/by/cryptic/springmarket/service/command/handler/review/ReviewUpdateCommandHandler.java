package by.cryptic.springmarket.service.command.handler.review;

import by.cryptic.springmarket.event.review.ReviewUpdatedEvent;
import by.cryptic.springmarket.mapper.ReviewMapper;
import by.cryptic.springmarket.model.write.AppUser;
import by.cryptic.springmarket.model.write.Review;
import by.cryptic.springmarket.repository.write.ReviewRepository;
import by.cryptic.springmarket.service.command.ReviewUpdateCommand;
import by.cryptic.springmarket.service.command.handler.CommandHandler;
import by.cryptic.springmarket.util.AuthUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"reviews"})
public class ReviewUpdateCommandHandler implements CommandHandler<ReviewUpdateCommand> {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final AuthUtil authUtil;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @CachePut(key = "'review:' + #dto.reviewId()")
    public void handle(ReviewUpdateCommand dto) {
        AppUser user = authUtil.getUserFromContext();
        Review review = reviewRepository.findById(dto.reviewId())
                .orElseThrow(() -> new EntityNotFoundException
                        ("Review not found with id: %s".formatted(dto.reviewId())));
        if (user.getReviews().contains(review)) {
            reviewMapper.updateEntity(review, dto);
        }
        eventPublisher.publishEvent(ReviewUpdatedEvent.builder()
                .title(review.getTitle())
                .image(review.getImage())
                .rating(review.getRating())
                .reviewId(dto.reviewId())
                .productId(dto.productId())
                .description(review.getDescription())
                .updatedBy(review.getUpdatedBy())
                .build());
    }
}
