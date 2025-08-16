package by.cryptic.reviewservice.service.query;

import by.cryptic.reviewservice.mapper.ReviewMapper;
import by.cryptic.reviewservice.model.read.ReviewView;
import by.cryptic.reviewservice.repository.read.ReviewViewRepository;
import by.cryptic.reviewservice.service.query.handler.ReviewGetAllQueryHandler;
import by.cryptic.utils.DTO.ReviewDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ReviewGetAllQueryHandlerTest {

    @Mock
    private ReviewViewRepository reviewViewRepository;

    @InjectMocks
    private ReviewGetAllQueryHandler reviewGetAllQueryHandler;

    @Test
    void getAllReviews_whenProductExists_shouldReturnAllReviews() {
        //Arrange
        UUID productId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();
        ReviewView reviewView = ReviewView.builder()
                .reviewId(reviewId)
                .productId(productId)
                .title("testReview")
                .rating(3.4)
                .build();
        ReviewDTO reviewDTO = ReviewMapper.toDto(reviewView);
        Mockito.when(reviewViewRepository.findAll()).thenReturn(Collections.singletonList(reviewView));
        //Act
        List<ReviewDTO> result = reviewGetAllQueryHandler.handle(productId);
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
        assertThrows(EntityNotFoundException.class, () -> reviewGetAllQueryHandler.handle(UUID.randomUUID()));
        Mockito.verify(reviewViewRepository, Mockito.times(1)).findAll();
        Mockito.verifyNoMoreInteractions(reviewViewRepository);
    }

}