package by.cryptic.reviewservice.service.command;

import by.cryptic.reviewservice.client.ProductServiceClient;
import by.cryptic.reviewservice.mapper.ReviewMapper;
import by.cryptic.reviewservice.model.write.Review;
import by.cryptic.reviewservice.publisher.ReviewEventPublisher;
import by.cryptic.reviewservice.repository.write.ReviewRepository;
import by.cryptic.reviewservice.service.command.handler.ReviewCreateCommandHandler;
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
    private ProductServiceClient productServiceClient;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    //TODO написать тесты под рейтинг онли отзывы и протестить все остальные тесты после добавление доков а также переделать it тесты

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
        ProductDTO productDTO = new ProductDTO("name", BigDecimal.ONE, 42, "desc",
                "img/url", UUID.randomUUID());
        Mockito.when(reviewRepository.save(any(Review.class))).thenReturn(review);
        Mockito.when(cacheManager.getCache("reviews")).thenReturn(cache);
        Mockito.when(productServiceClient.getProductById(productId)).thenReturn(ResponseEntity.ok(productDTO));
        //Act
        reviewCreateCommandHandler.handle(reviewCreateCommand);
        //Assert
        Mockito.verify(cacheManager).getCache("reviews");
        Mockito.verify(cache, Mockito.times(1)).put(startsWith("review:"), any());
        Mockito.verify(reviewRepository, Mockito.times(1)).save(any(Review.class));
        Mockito.verify(reviewEventPublisher, Mockito.times(1)).saveReviewView(any());
    }
}