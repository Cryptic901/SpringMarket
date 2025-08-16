package by.cryptic.reviewservice.service.query;

import by.cryptic.reviewservice.mapper.ReviewMapper;
import by.cryptic.reviewservice.model.read.ReviewView;
import by.cryptic.reviewservice.model.write.Review;
import by.cryptic.reviewservice.repository.read.ReviewViewRepository;
import by.cryptic.reviewservice.service.query.handler.ReviewGetByIdQueryHandler;
import by.cryptic.utils.DTO.ReviewDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@Transactional
class ReviewGetByIdQueryHandlerTest {

    @Mock
    private ReviewViewRepository reviewRepository;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private ReviewGetByIdQueryHandler reviewGetByIdQueryHandler;

    @Test
    void getReviewById_withValidUUID_shouldReturnReview() {
        //Arrange
        UUID reviewId = UUID.randomUUID();
        String cacheKey = "review:" + reviewId;
        Mockito.when(cacheManager.getCache("reviews")).thenReturn(cache);
        Mockito.when(cache.get(cacheKey, Review.class)).thenReturn(null);

        UUID productId = UUID.randomUUID();
        Review review = Review.builder()
                .id(reviewId)
                .productId(productId)
                .title("testReview")
                .rating(3.4)
                .build();
        ReviewView reviewView = ReviewView.builder()
                .reviewId(reviewId)
                .productId(productId)
                .title("testReview")
                .rating(3.4)
                .build();
        ReviewDTO reviewDTO = ReviewMapper.toDto(review);
        Mockito.when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(reviewView));
        //Act
        ReviewDTO result = reviewGetByIdQueryHandler.handle(reviewId);
        //Assert
        assertEquals(reviewDTO, result);
        Mockito.verify(cacheManager).getCache("reviews");
        Mockito.verify(reviewRepository, Mockito.times(1)).findById(reviewId);
        Mockito.verifyNoMoreInteractions(reviewRepository);
    }

    @Test
    void getReviewById_withInvalidUUID_shouldThrowEntityNotFoundException() {
        //Arrange
        UUID reviewId = UUID.randomUUID();
        Mockito.when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());
        //Act
        //Assert
        assertThrows(EntityNotFoundException.class, () -> reviewGetByIdQueryHandler.handle(reviewId));
        Mockito.verify(reviewRepository, Mockito.times(1)).findById(reviewId);
        Mockito.verifyNoMoreInteractions(reviewRepository);
    }
}