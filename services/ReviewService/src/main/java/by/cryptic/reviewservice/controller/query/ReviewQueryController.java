package by.cryptic.reviewservice.controller.query;

import by.cryptic.reviewservice.service.query.ReviewGetAllQuery;
import by.cryptic.reviewservice.service.query.ReviewGetByIdQuery;
import by.cryptic.utils.DTO.ReviewDTO;
import by.cryptic.reviewservice.service.query.handler.ReviewGetAllQueryHandler;
import by.cryptic.reviewservice.service.query.handler.ReviewGetByIdQueryHandler;
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
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewQueryController {

    private final ReviewGetAllQueryHandler reviewGetAllQueryHandler;
    private final ReviewGetByIdQueryHandler reviewGetByIdQueryHandler;

    @GetMapping("/product/{id}")
    @Operation(summary = "Get all reviews by product id", description = "return all reviews that belong to a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reviews found"),
            @ApiResponse(responseCode = "404", description = "Reviews not found"),
    })
    public ResponseEntity<List<ReviewDTO>> getAllReviews(@PathVariable UUID id) {
        return ResponseEntity.ok(reviewGetAllQueryHandler.handle(new ReviewGetAllQuery(id)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get review by id", description = "return review by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review found"),
            @ApiResponse(responseCode = "404", description = "Review not found"),
    })
    public ResponseEntity<ReviewDTO> getReview(@PathVariable UUID id) {
        return ResponseEntity.ok(reviewGetByIdQueryHandler.handle(new ReviewGetByIdQuery(id)));
    }
}
