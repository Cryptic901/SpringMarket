package by.cryptic.springmarket.service.command.handler.review;

import by.cryptic.springmarket.event.review.ReviewCreatedEvent;
import by.cryptic.springmarket.model.write.AppUser;
import by.cryptic.springmarket.model.write.Product;
import by.cryptic.springmarket.model.write.Review;
import by.cryptic.springmarket.repository.write.ProductRepository;
import by.cryptic.springmarket.service.command.ReviewCreateCommand;
import by.cryptic.springmarket.service.command.handler.CommandHandler;
import by.cryptic.springmarket.util.AuthUtil;
import jakarta.persistence.EntityNotFoundException;
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

    private final ProductRepository productRepository;
    private final AuthUtil authUtil;
    private final ApplicationEventPublisher eventPublisher;
    private final CacheManager cacheManager;

    @Transactional
    public void handle(ReviewCreateCommand dto) {
        AppUser user = authUtil.getUserFromContext();
        Product product = productRepository.findById(dto.productId())
                .orElseThrow(() -> new EntityNotFoundException
                        ("Product not found with id: %s".formatted(dto.productId())));
        Review review = Review.builder()
                .title(dto.title())
                .rating(dto.rating())
                .description(dto.description())
                .image(dto.image())
                .product(product)
                .build();
        user.getReviews().add(review);
        eventPublisher.publishEvent(ReviewCreatedEvent.builder()
                .reviewId(review.getId())
                .productId(product.getId())
                .createdBy(review.getCreatedBy())
                .title(review.getTitle())
                .description(review.getDescription())
                .image(review.getImage())
                .build());
        Objects.requireNonNull(cacheManager.getCache("reviews"))
                .put("review:" + dto.title() + '-' + dto.description(), review);
    }
}
