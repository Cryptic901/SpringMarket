package by.cryptic.springmarket.service.command.handler.review;

import by.cryptic.springmarket.event.review.ReviewDeletedEvent;
import by.cryptic.springmarket.model.write.AppUser;
import by.cryptic.springmarket.model.write.Review;
import by.cryptic.springmarket.repository.write.ReviewRepository;
import by.cryptic.springmarket.service.command.ReviewDeleteCommand;
import by.cryptic.springmarket.service.command.handler.CommandHandler;
import by.cryptic.springmarket.util.AuthUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"reviews"})
public class ReviewDeleteCommandHandler implements CommandHandler<ReviewDeleteCommand> {

    private final AuthUtil authUtil;
    private final ReviewRepository reviewRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @CacheEvict(key = "'review:' + #command.reviewId()")
    public void handle(ReviewDeleteCommand command) {
        AppUser user = authUtil.getUserFromContext();
        Review review = reviewRepository.findById(command.reviewId())
                .orElseThrow(() -> new EntityNotFoundException
                        ("Review not found with id: %s".formatted(command.reviewId())));
        if (user.getReviews().contains(review)) {
            reviewRepository.deleteById(command.reviewId());
        }
        eventPublisher.publishEvent(new ReviewDeletedEvent(command.reviewId()));
    }
}
