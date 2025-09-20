package by.cryptic.reviewservice.service.command;

import by.cryptic.reviewservice.mapper.ReviewMapper;
import by.cryptic.reviewservice.model.write.Review;
import by.cryptic.reviewservice.publisher.ReviewEventPublisher;
import by.cryptic.reviewservice.repository.write.ReviewRepository;
import by.cryptic.reviewservice.service.command.handler.ReviewCreateCommandHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.startsWith;

@ExtendWith(MockitoExtension.class)
@Import(ReviewMapper.class)
class ReviewCreateCommandHandlerTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewEventPublisher reviewEventPublisher;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private ReviewCreateCommandHandler reviewCreateCommandHandler;

    @Test
    void createReview_whenFieldsAreOk_shouldSaveReview() {
        //Arrange
        UUID reviewId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Review review = Review.builder()
                .id(reviewId)
                .productId(productId)
                .title("testReview")
                .description("testDesc")
                .image("test/img")
                .rating(3.4)
                .build();
        ReviewCreateCommand reviewCreateCommand = new ReviewCreateCommand(review.getTitle(),
                review.getDescription(),
                review.getRating(),
                review.getImage(),
                productId,
                userId);
        Mockito.when(reviewRepository.save(any(Review.class))).thenReturn(review);
        Mockito.when(cacheManager.getCache("reviews")).thenReturn(cache);
        //Act
        reviewCreateCommandHandler.handle(reviewCreateCommand);
        //Assert
        Mockito.verify(cacheManager).getCache("reviews");
        Mockito.verify(cache, Mockito.times(1)).put(startsWith("review:"), any());
        Mockito.verify(reviewRepository, Mockito.times(1)).save(any(Review.class));
        Mockito.verify(reviewEventPublisher, Mockito.times(1)).saveReviewView(any());
    }
}