package by.cryptic.springmarket.controller.query;

import by.cryptic.springmarket.dto.ReviewDTO;
import by.cryptic.springmarket.service.query.handler.review.ReviewGetAllQueryHandler;
import by.cryptic.springmarket.service.query.handler.review.ReviewGetByIdQueryHandler;
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
    public ResponseEntity<List<by.cryptic.springmarket.service.query.ReviewDTO>> getAllReviews(@PathVariable UUID id) {
        return ResponseEntity.ok(reviewGetAllQueryHandler.handle(id));
    }

    @GetMapping("/review/{id}")
    public ResponseEntity<ReviewDTO> getReview(@PathVariable UUID id) {
        return ResponseEntity.ok(reviewGetByIdQueryHandler.handle(id));
    }
}
