package by.cryptic.reviewservice.service.command;

import by.cryptic.reviewservice.client.ProductServiceClient;
import by.cryptic.reviewservice.mapper.ReviewMapper;
import by.cryptic.reviewservice.model.write.RatingOnlyReview;
import by.cryptic.reviewservice.publisher.ReviewEventPublisher;
import by.cryptic.reviewservice.repository.write.RatingOnlyReviewRepository;
import by.cryptic.reviewservice.service.command.handler.RatingOnlyReviewCreateCommandHandler;
import by.cryptic.utils.DTO.ProductDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.startsWith;

@ExtendWith(MockitoExtension.class)
@Import(ReviewMapper.class)
class RatingOnlyReviewCreateCommandHandlerTest {

    @Mock
    private RatingOnlyReviewRepository reviewRepository;

    @Mock
    private ReviewEventPublisher reviewEventPublisher;

    @Mock
    private ProductServiceClient productServiceClient;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private RatingOnlyReviewCreateCommandHandler reviewCreateCommandHandler;

    @Test
    void createReview_whenFieldsAreOk_shouldSaveReview() {
        //Arrange
        UUID reviewId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        RatingOnlyReview review = RatingOnlyReview.builder()
                .id(reviewId)
                .productId(productId)
                .rating(3.4)
                .createdAt(LocalDateTime.now())
                .createdBy(userId)
                .build();
        RatingOnlyReviewCreateCommand reviewCreateCommand = new RatingOnlyReviewCreateCommand(
                review.getRating(),
                productId,
                userId);
        ProductDTO productDTO = new ProductDTO("name", BigDecimal.ONE, 42, "desc",
                "img/url", UUID.randomUUID());
        Mockito.when(reviewRepository.save(any(RatingOnlyReview.class))).thenReturn(review);
        Mockito.when(cacheManager.getCache("reviews")).thenReturn(cache);
        Mockito.when(productServiceClient.getProductById(productId)).thenReturn(ResponseEntity.ok(productDTO));
        //Act
        reviewCreateCommandHandler.handle(reviewCreateCommand);
        //Assert
        Mockito.verify(cacheManager).getCache("reviews");
        Mockito.verify(cache, Mockito.times(1)).put(startsWith("review:"), any());
        Mockito.verify(reviewRepository, Mockito.times(1)).save(any(RatingOnlyReview.class));
        Mockito.verify(reviewEventPublisher, Mockito.times(1)).saveRatingOnlyReviewView(any());
    }
}