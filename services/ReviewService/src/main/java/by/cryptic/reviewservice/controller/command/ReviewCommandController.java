package by.cryptic.reviewservice.controller.command;

import by.cryptic.reviewservice.dto.ReviewCreateDTO;
import by.cryptic.reviewservice.dto.ReviewUpdateDTO;
import by.cryptic.reviewservice.service.command.ReviewCreateCommand;
import by.cryptic.reviewservice.service.command.ReviewDeleteCommand;
import by.cryptic.reviewservice.service.command.ReviewUpdateCommand;
import by.cryptic.reviewservice.service.command.handler.ReviewCreateCommandHandler;
import by.cryptic.reviewservice.service.command.handler.ReviewDeleteCommandHandler;
import by.cryptic.reviewservice.service.command.handler.ReviewUpdateCommandHandler;
import by.cryptic.security.JwtUtil;
import by.cryptic.utils.DTO.ReviewDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
    public ResponseEntity<ReviewDTO> createReview(
            @RequestBody @Valid ReviewCreateDTO createReviewDTO, @AuthenticationPrincipal Jwt jwt) {
        reviewCreateCommandHandler.handle(new ReviewCreateCommand(
                createReviewDTO.title(),
                createReviewDTO.description(),
                createReviewDTO.rating(),
                createReviewDTO.image(),
                createReviewDTO.productId(),
                JwtUtil.extractUserId(jwt)
        ));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable UUID id,
                                                  @RequestBody @Valid ReviewUpdateDTO updateReviewDTO,
                                                  @AuthenticationPrincipal Jwt jwt) {
        reviewUpdateCommandHandler.handle(new ReviewUpdateCommand(
                id,
                updateReviewDTO.title(),
                updateReviewDTO.rating(),
                updateReviewDTO.description(),
                updateReviewDTO.image(),
                JwtUtil.extractUserId(jwt)
        ));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        reviewDeleteCommandHandler.handle(new ReviewDeleteCommand(id, JwtUtil.extractUserId(jwt)));
        return ResponseEntity.noContent().build();
    }
}
