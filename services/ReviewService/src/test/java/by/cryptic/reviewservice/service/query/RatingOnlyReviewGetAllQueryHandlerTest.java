package by.cryptic.reviewservice.service.query;

import by.cryptic.reviewservice.mapper.ReviewMapper;
import by.cryptic.reviewservice.model.read.RatingOnlyReviewView;
import by.cryptic.reviewservice.repository.read.RatingOnlyReviewViewRepository;
import by.cryptic.reviewservice.service.query.handler.RatingOnlyReviewGetAllQueryHandler;
import by.cryptic.utils.DTO.RatingOnlyReviewDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class RatingOnlyReviewGetAllQueryHandlerTest {

    @Mock
    private RatingOnlyReviewViewRepository reviewViewRepository;

    @InjectMocks
    private RatingOnlyReviewGetAllQueryHandler reviewGetAllQueryHandler;

    @Test
    void getAllReviews_whenProductExists_shouldReturnAllReviews() {
        //Arrange
        UUID productId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();
        RatingOnlyReviewView reviewView = RatingOnlyReviewView.builder()
                .reviewId(reviewId)
                .productId(productId)
                .rating(3.4)
                .createdAt(LocalDateTime.now())
                .createdBy(UUID.randomUUID())
                .build();
        RatingOnlyReviewDTO reviewDTO = ReviewMapper.toDto(reviewView);
        Mockito.when(reviewViewRepository.findAll()).thenReturn(Collections.singletonList(reviewView));
        //Act
        List<RatingOnlyReviewDTO> result = reviewGetAllQueryHandler.handle(new ReviewGetAllQuery(productId));
        //Assert
        assertEquals(Collections.singletonList(reviewDTO), result);
        Mockito.verify(reviewViewRepository, Mockito.times(1)).findAll();
        Mockito.verifyNoMoreInteractions(reviewViewRepository);
    }

    @Test
    void getAllReviews_shouldReturnEntityNotFoundException() {
        //Arrange
        Mockito.when(reviewViewRepository.findAll()).thenReturn(Collections.emptyList());
        //Act
        //Assert
        assertThrows(EntityNotFoundException.class, () -> reviewGetAllQueryHandler.handle(new ReviewGetAllQuery(UUID.randomUUID())));
        Mockito.verify(reviewViewRepository, Mockito.times(1)).findAll();
        Mockito.verifyNoMoreInteractions(reviewViewRepository);
    }

}