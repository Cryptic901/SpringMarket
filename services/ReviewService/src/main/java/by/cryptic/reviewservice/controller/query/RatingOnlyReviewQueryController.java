package by.cryptic.reviewservice.controller.query;

import by.cryptic.reviewservice.service.query.ReviewGetAllQuery;
import by.cryptic.reviewservice.service.query.ReviewGetByIdQuery;
import by.cryptic.reviewservice.service.query.handler.RatingOnlyReviewGetAllQueryHandler;
import by.cryptic.reviewservice.service.query.handler.RatingOnlyReviewGetByIdQueryHandler;
import by.cryptic.utils.DTO.RatingOnlyReviewDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews/quick")
@RequiredArgsConstructor
public class RatingOnlyReviewQueryController {

    private final RatingOnlyReviewGetAllQueryHandler reviewGetAllQueryHandler;
    private final RatingOnlyReviewGetByIdQueryHandler reviewGetByIdQueryHandler;

    @GetMapping("/product/{id}")
    @Operation(summary = "Get all rating only reviews by product id", description = "return all rating only reviews that belong to a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rating only reviews found"),
            @ApiResponse(responseCode = "404", description = "Rating only reviews not found"),
    })
    public ResponseEntity<List<RatingOnlyReviewDTO>> getAllReviews(@PathVariable UUID id) {
        return ResponseEntity.ok(reviewGetAllQueryHandler.handle(new ReviewGetAllQuery(id)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get rating only reviews by id", description = "return rating only reviews by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rating only reviews found"),
            @ApiResponse(responseCode = "404", description = "Rating only reviews not found"),
    })
    public ResponseEntity<RatingOnlyReviewDTO> getReview(@PathVariable UUID id) {
        return ResponseEntity.ok(reviewGetByIdQueryHandler.handle(new ReviewGetByIdQuery(id)));
    }
}
