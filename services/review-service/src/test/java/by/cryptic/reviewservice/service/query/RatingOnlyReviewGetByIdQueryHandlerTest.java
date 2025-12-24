package by.cryptic.reviewservice.service.query;

import by.cryptic.reviewservice.mapper.ReviewMapper;
import by.cryptic.reviewservice.model.read.RatingOnlyReviewView;
import by.cryptic.reviewservice.model.write.RatingOnlyReview;
import by.cryptic.reviewservice.repository.read.RatingOnlyReviewViewRepository;
import by.cryptic.reviewservice.service.query.handler.RatingOnlyReviewGetByIdQueryHandler;
import by.cryptic.utils.DTO.RatingOnlyReviewDTO;
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

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@Transactional
class RatingOnlyReviewGetByIdQueryHandlerTest {

    @Mock
    private RatingOnlyReviewViewRepository reviewRepository;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private RatingOnlyReviewGetByIdQueryHandler reviewGetByIdQueryHandler;

    @Test
    void getReviewById_withValidUUID_shouldReturnReview() {
        //Arrange
        UUID reviewId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String cacheKey = "review:" + reviewId;
        LocalDateTime now = LocalDateTime.now();
        Mockito.when(cacheManager.getCache("reviews")).thenReturn(cache);
        Mockito.when(cache.get(cacheKey, RatingOnlyReview.class)).thenReturn(null);

        UUID productId = UUID.randomUUID();
        RatingOnlyReview review = RatingOnlyReview.builder()
                .id(reviewId)
                .productId(productId)
                .rating(3.4)
                .createdBy(userId)
                .createdAt(now)
                .build();
        RatingOnlyReviewView reviewView = RatingOnlyReviewView.builder()
                .reviewId(reviewId)
                .productId(productId)
                .rating(3.4)
                .createdAt(now)
                .createdBy(userId)
                .build();
        RatingOnlyReviewDTO reviewDTO = ReviewMapper.toDto(review);
        Mockito.when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(reviewView));
        //Act
        RatingOnlyReviewDTO result = reviewGetByIdQueryHandler.handle(new ReviewGetByIdQuery(reviewId));
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
        assertThrows(EntityNotFoundException.class, () -> reviewGetByIdQueryHandler.handle(new ReviewGetByIdQuery(reviewId)));
        Mockito.verify(reviewRepository, Mockito.times(1)).findById(reviewId);
        Mockito.verifyNoMoreInteractions(reviewRepository);
    }
}