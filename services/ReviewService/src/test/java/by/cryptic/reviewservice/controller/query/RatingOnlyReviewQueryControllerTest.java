package by.cryptic.reviewservice.controller.query;

import by.cryptic.reviewservice.mapper.ReviewMapper;
import by.cryptic.reviewservice.model.write.RatingOnlyReview;
import by.cryptic.reviewservice.service.query.ReviewGetByIdQuery;
import by.cryptic.reviewservice.service.query.handler.RatingOnlyReviewGetAllQueryHandler;
import by.cryptic.reviewservice.service.query.handler.RatingOnlyReviewGetByIdQueryHandler;
import by.cryptic.utils.DTO.RatingOnlyReviewDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(printOnlyOnFailure = false)
@WebMvcTest(RatingOnlyReviewQueryController.class)
@ActiveProfiles("mongo")
@Import(ReviewMapper.class)
class RatingOnlyReviewQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RatingOnlyReviewGetAllQueryHandler reviewGetAllQueryHandler;

    @MockitoBean
    private RatingOnlyReviewGetByIdQueryHandler reviewGetByIdQueryHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void getAllReviews_withProductThatExists_shouldReturnAllReviews() throws Exception {
        //Arrange
        UUID reviewId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        RatingOnlyReview review = RatingOnlyReview.builder()
                .id(reviewId)
                .productId(productId)
                .rating(3.4)
                .createdBy(userId)
                .createdAt(LocalDateTime.now())
                .build();
        List<RatingOnlyReviewDTO> reviewDTOS = new ArrayList<>();
        reviewDTOS.add(ReviewMapper.toDto(review));
        Mockito.when(reviewGetAllQueryHandler.handle(any())).thenReturn(reviewDTOS);
        //Act
        var mvc = mockMvc.perform(get("/api/v1/reviews/quick/product/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(reviewDTOS)));
        //Assert
        String result = mvc.andReturn().getResponse().getContentAsString();
        Assertions.assertEquals(objectMapper.writeValueAsString(reviewDTOS), result);
        Mockito.verify(reviewGetAllQueryHandler, Mockito.times(1)).handle(any());
        Mockito.verifyNoMoreInteractions(reviewGetAllQueryHandler);
    }

    @Test
    void getAllReviews_withNotAuthorizedUser_shouldReturnUnauthorized() throws Exception {
        //Arrange
        UUID reviewId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        RatingOnlyReview review = RatingOnlyReview.builder()
                .id(reviewId)
                .productId(productId)
                .rating(3.4)
                .createdBy(userId)
                .createdAt(LocalDateTime.now())
                .build();
        List<RatingOnlyReviewDTO> reviewDTOS = new ArrayList<>();
        reviewDTOS.add(ReviewMapper.toDto(review));
        Mockito.when(reviewGetAllQueryHandler.handle(any())).thenReturn(reviewDTOS);
        //Act
        //Assert
        mockMvc.perform(get("/api/v1/reviews/quick/product/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

    }

    @Test
    @WithMockUser
    void getReviewById_withProductThatExistsAndAuthorizedUser_shouldReturnReview() throws Exception {
        //Arrange
        UUID reviewId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        RatingOnlyReview review = RatingOnlyReview.builder()
                .id(reviewId)
                .productId(productId)
                .rating(3.4)
                .createdBy(userId)
                .createdAt(LocalDateTime.now())
                .build();
        RatingOnlyReviewDTO reviewDTO = ReviewMapper.toDto(review);
        Mockito.when(reviewGetByIdQueryHandler.handle(new ReviewGetByIdQuery(reviewId))).thenReturn(reviewDTO);
        //Act
        mockMvc.perform(get("/api/v1/reviews/quick/" + reviewId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(reviewDTO)));
        //Assert
        Mockito.verify(reviewGetByIdQueryHandler, times(1)).handle(new ReviewGetByIdQuery(reviewId));
        Mockito.verifyNoMoreInteractions(reviewGetByIdQueryHandler);
    }

    @Test
    void getReviewById_withProductThatExistsAndNotAuthorizedUser_shouldReturnUnauthorized() throws Exception {
        //Arrange
        UUID reviewId = UUID.randomUUID();
        //Act
        //Assert
        mockMvc.perform(get("/api/v1/reviews/quick/" + reviewId))
                .andExpect(status().isUnauthorized());
    }
}