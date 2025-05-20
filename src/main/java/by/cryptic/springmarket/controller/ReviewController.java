package by.cryptic.springmarket.controller;

import by.cryptic.springmarket.dto.CreateReviewDTO;
import by.cryptic.springmarket.dto.FullReviewDTO;
import by.cryptic.springmarket.dto.ReviewDTO;
import by.cryptic.springmarket.dto.UpdateReviewDTO;
import by.cryptic.springmarket.model.AppUser;
import by.cryptic.springmarket.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/product/{id}")
    public ResponseEntity<List<ReviewDTO>> getAllReviews(@PathVariable UUID id) {
        return ResponseEntity.ok(reviewService.getAllReviews(id));
    }

    @GetMapping("/review/{id}")
    public ResponseEntity<FullReviewDTO> getReview(@PathVariable UUID id) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }

    @PostMapping
    public ResponseEntity<FullReviewDTO> createReview(
            @RequestBody @Valid CreateReviewDTO createReviewDTO,
            @AuthenticationPrincipal AppUser appUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.createReview(createReviewDTO, appUser));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FullReviewDTO> updateReview(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateReviewDTO updateReviewDTO,
            @AuthenticationPrincipal AppUser appUser) {
        return ResponseEntity.ok(reviewService.updateReview(id, updateReviewDTO, appUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable UUID id,
                                             @AuthenticationPrincipal AppUser appUser) {
        reviewService.deleteReview(id, appUser);
        return ResponseEntity.noContent().build();
    }
}
