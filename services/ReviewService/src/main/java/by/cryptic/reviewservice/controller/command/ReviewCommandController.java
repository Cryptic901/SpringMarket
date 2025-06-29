package by.cryptic.reviewservice.controller.command;

import by.cryptic.reviewservice.dto.ReviewDTO;
import by.cryptic.reviewservice.service.command.ReviewCreateCommand;
import by.cryptic.reviewservice.service.command.ReviewDeleteCommand;
import by.cryptic.reviewservice.service.command.ReviewUpdateCommand;
import by.cryptic.reviewservice.service.command.handler.ReviewCreateCommandHandler;
import by.cryptic.reviewservice.service.command.handler.ReviewDeleteCommandHandler;
import by.cryptic.reviewservice.service.command.handler.ReviewUpdateCommandHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewCommandController {

    private final ReviewCreateCommandHandler reviewCreateCommandHandler;
    private final ReviewUpdateCommandHandler reviewUpdateCommandHandler;
    private final ReviewDeleteCommandHandler reviewDeleteCommandHandler;

    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(
            @RequestBody @Valid ReviewCreateCommand createReviewDTO) {
        reviewCreateCommandHandler.handle(createReviewDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping
    public ResponseEntity<ReviewDTO> updateReview(
            @RequestBody @Valid ReviewUpdateCommand updateReviewDTO,
            @RequestHeader("X-User-ID") UUID userId) throws AccessDeniedException {
        reviewUpdateCommandHandler.handle(new ReviewUpdateCommand(
                updateReviewDTO.reviewId(),
                updateReviewDTO.title(),
                updateReviewDTO.rating(),
                updateReviewDTO.description(),
                updateReviewDTO.image(),
                userId
        ));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable UUID id, @RequestHeader("X-User-ID") UUID userId) {
        reviewDeleteCommandHandler.handle(new ReviewDeleteCommand(id, userId));
        return ResponseEntity.noContent().build();
    }
}
