package by.cryptic.reviewservice.controller.query;

import by.cryptic.reviewservice.dto.ReviewDTO;
import by.cryptic.reviewservice.service.query.ReviewQuery;
import by.cryptic.reviewservice.service.query.handler.ReviewGetAllQueryHandler;
import by.cryptic.reviewservice.service.query.handler.ReviewGetByIdQueryHandler;
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
    public ResponseEntity<List<ReviewQuery>> getAllReviews(@PathVariable UUID id) {
        return ResponseEntity.ok(reviewGetAllQueryHandler.handle(id));
    }

    @GetMapping("/review/{id}")
    public ResponseEntity<ReviewDTO> getReview(@PathVariable UUID id) {
        return ResponseEntity.ok(reviewGetByIdQueryHandler.handle(id));
    }
}
