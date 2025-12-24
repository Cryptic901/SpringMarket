package by.cryptic.reviewservice.controller.command;

import by.cryptic.reviewservice.dto.RatingOnlyReviewCreateDTO;
import by.cryptic.reviewservice.dto.RatingOnlyReviewUpdateDTO;
import by.cryptic.reviewservice.model.write.RatingOnlyReview;
import by.cryptic.reviewservice.service.command.handler.RatingOnlyReviewCreateCommandHandler;
import by.cryptic.reviewservice.service.command.handler.RatingOnlyReviewDeleteCommandHandler;
import by.cryptic.reviewservice.service.command.handler.RatingOnlyReviewUpdateCommandHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(printOnlyOnFailure = false)
@WebMvcTest(RatingOnlyReviewCommandController.class)
@ActiveProfiles("jpa")
class RatingOnlyReviewCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RatingOnlyReviewCreateCommandHandler reviewCreateCommandHandler;

    @MockitoBean
    private RatingOnlyReviewUpdateCommandHandler reviewUpdateCommandHandler;

    @MockitoBean
    private RatingOnlyReviewDeleteCommandHandler reviewDeleteCommandHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createReview_withAuthorizedUser_shouldCreateReview() throws Exception {
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
        RatingOnlyReviewCreateDTO reviewCreateDTO = new RatingOnlyReviewCreateDTO(productId,
                review.getRating());
        String json = objectMapper.writeValueAsString(reviewCreateDTO);
        //Act
        mockMvc.perform(post("/api/v1/reviews/quick")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", UUID.randomUUID())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
        //Assert
        verify(reviewCreateCommandHandler, times(1)).handle(any());
        verifyNoMoreInteractions(reviewCreateCommandHandler);
    }

    @Test
    void createReview_withNotAuthorizedUser_shouldReturnForbidden() throws Exception {
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
        RatingOnlyReviewCreateDTO reviewCreateDTO = new RatingOnlyReviewCreateDTO(productId,
                review.getRating());
        String json = objectMapper.writeValueAsString(reviewCreateDTO);
        //Act
        mockMvc.perform(post("/api/v1/reviews/quick")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
        //Assert
        verifyNoMoreInteractions(reviewCreateCommandHandler);
    }

    @Test
    void updateReview_withAuthorizedUser_shouldUpdateReview() throws Exception {
        //Arrange
        UUID reviewId = UUID.randomUUID();
        RatingOnlyReviewUpdateDTO reviewUpdateDTO = new RatingOnlyReviewUpdateDTO
                (3.4);
        String json = objectMapper.writeValueAsString(reviewUpdateDTO);
        //Act
        mockMvc.perform(patch("/api/v1/reviews/quick/" + reviewId)
                        .with(jwt().jwt(jwt -> jwt.claim("sub", UUID.randomUUID())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is2xxSuccessful());
        //Assert
        verify(reviewUpdateCommandHandler, times(1)).handle(any());
        verifyNoMoreInteractions(reviewUpdateCommandHandler);
    }

    @Test
    void updateReview_withNotAuthorizedUser_shouldReturnForbidden() throws Exception {
        //Arrange
        UUID reviewId = UUID.randomUUID();
        RatingOnlyReviewUpdateDTO reviewUpdateDTO = new RatingOnlyReviewUpdateDTO
                (3.4);
        String json = objectMapper.writeValueAsString(reviewUpdateDTO);
        //Act
        mockMvc.perform(patch("/api/v1/reviews/quick/" + reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
        //Assert
        verifyNoMoreInteractions(reviewUpdateCommandHandler);
    }

    @Test
    void deleteReview_withAuthorizedUser_shouldDeleteReview() throws Exception {
        //Arrange
        UUID reviewId = UUID.randomUUID();
        //Act
        mockMvc.perform(delete("/api/v1/reviews/quick/" + reviewId)
                        .with(jwt().jwt(jwt -> jwt.claim("sub", UUID.randomUUID())))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        //Assert
        verify(reviewDeleteCommandHandler, times(1)).handle(any());
        verifyNoMoreInteractions(reviewDeleteCommandHandler);
    }

    @Test
    void deleteReview_withNotAuthorizedUser_shouldReturnForbidden() throws Exception {
        //Arrange
        UUID reviewId = UUID.randomUUID();
        //Act
        mockMvc.perform(delete("/api/v1/reviews/quick/" + reviewId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        //Assert
        verifyNoMoreInteractions(reviewDeleteCommandHandler);
    }
}