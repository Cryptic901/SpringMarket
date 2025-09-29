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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Create review", description = "creating review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review created"),
            @ApiResponse(responseCode = "400", description = "Request parameters are incorrect"),
            @ApiResponse(responseCode = "404", description = "Product for review not found"),
            @ApiResponse(responseCode = "503", description = "The creation failed because the server is down")
    })
    public ResponseEntity<Void> createReview(
            @RequestBody @Valid ReviewCreateDTO createReviewDTO,
            @AuthenticationPrincipal Jwt jwt) {
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
    @Operation(summary = "Update review", description = "updating review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Review updated"),
            @ApiResponse(responseCode = "404", description = "Product for review not found"),
            @ApiResponse(responseCode = "503", description = "The updating failed because the server is down")
    })
    public ResponseEntity<Void> updateReview(@PathVariable UUID id,
                                             @RequestBody ReviewUpdateDTO updateReviewDTO,
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
    @Operation(summary = "Delete review", description = "deleting review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Review deleted"),
            @ApiResponse(responseCode = "404", description = "Review for deleting not found"),
            @ApiResponse(responseCode = "503", description = "The deleting failed because the server is down")
    })
    public ResponseEntity<Void> deleteReview(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        reviewDeleteCommandHandler.handle(new ReviewDeleteCommand(id, JwtUtil.extractUserId(jwt)));
        return ResponseEntity.noContent().build();
    }
}
