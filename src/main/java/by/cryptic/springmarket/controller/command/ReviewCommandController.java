package by.cryptic.springmarket.controller.command;

import by.cryptic.springmarket.dto.FullReviewDTO;
import by.cryptic.springmarket.service.command.ReviewCreateCommand;
import by.cryptic.springmarket.service.command.ReviewDeleteCommand;
import by.cryptic.springmarket.service.command.ReviewUpdateCommand;
import by.cryptic.springmarket.service.command.handler.review.ReviewCreateCommandHandler;
import by.cryptic.springmarket.service.command.handler.review.ReviewDeleteCommandHandler;
import by.cryptic.springmarket.service.command.handler.review.ReviewUpdateCommandHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewCommandController {

    private final ReviewCreateCommandHandler reviewCreateCommandHandler;
    private final ReviewUpdateCommandHandler reviewUpdateCommandHandler;
    private final ReviewDeleteCommandHandler reviewDeleteCommandHandler;

    @PostMapping
    public ResponseEntity<FullReviewDTO> createReview(
            @RequestBody @Valid ReviewCreateCommand createReviewDTO) {
        reviewCreateCommandHandler.handle(createReviewDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping
    public ResponseEntity<FullReviewDTO> updateReview(
            @RequestBody @Valid ReviewUpdateCommand updateReviewDTO) {
        reviewUpdateCommandHandler.handle(updateReviewDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable UUID id) {
        reviewDeleteCommandHandler.handle(new ReviewDeleteCommand(id));
        return ResponseEntity.noContent().build();
    }
}
