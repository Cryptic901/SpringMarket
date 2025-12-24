package by.cryptic.reviewservice.controller.command;

import by.cryptic.reviewservice.dto.RatingOnlyReviewCreateDTO;
import by.cryptic.reviewservice.dto.RatingOnlyReviewUpdateDTO;
import by.cryptic.reviewservice.service.command.RatingOnlyReviewCreateCommand;
import by.cryptic.reviewservice.service.command.RatingOnlyReviewDeleteCommand;
import by.cryptic.reviewservice.service.command.RatingOnlyReviewUpdateCommand;
import by.cryptic.reviewservice.service.command.handler.RatingOnlyReviewCreateCommandHandler;
import by.cryptic.reviewservice.service.command.handler.RatingOnlyReviewDeleteCommandHandler;
import by.cryptic.reviewservice.service.command.handler.RatingOnlyReviewUpdateCommandHandler;
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
@RequestMapping("/api/v1/reviews/quick")
@RequiredArgsConstructor
public class RatingOnlyReviewCommandController {

    private final RatingOnlyReviewCreateCommandHandler ratingOnlyReviewCreateCommandHandler;
    private final RatingOnlyReviewUpdateCommandHandler ratingOnlyReviewUpdateCommandHandler;
    private final RatingOnlyReviewDeleteCommandHandler ratingOnlyReviewDeleteCommandHandler;

    @PostMapping
    @Operation(summary = "Create rating only review", description = "creating rating only review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rating only review created"),
            @ApiResponse(responseCode = "400", description = "Request parameters are incorrect"),
            @ApiResponse(responseCode = "404", description = "Product for rating only review not found"),
            @ApiResponse(responseCode = "503", description = "The creation failed because the server is down")
    })
    public ResponseEntity<Void> createReview(
            @RequestBody @Valid RatingOnlyReviewCreateDTO ratingOnlyReviewDTO,
            @AuthenticationPrincipal Jwt jwt) {
        ratingOnlyReviewCreateCommandHandler.handle(new RatingOnlyReviewCreateCommand(
                ratingOnlyReviewDTO.getRating(),
                ratingOnlyReviewDTO.getProductId(),
                JwtUtil.extractUserId(jwt)
        ));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update rating only review", description = "updating rating only review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Rating only review updated"),
            @ApiResponse(responseCode = "404", description = "Product for rating only review not found"),
            @ApiResponse(responseCode = "503", description = "The updating failed because the server is down")
    })
    public ResponseEntity<Void> updateReview(@PathVariable UUID id,
                                             @RequestBody @Valid RatingOnlyReviewUpdateDTO updateReviewDTO,
                                             @AuthenticationPrincipal Jwt jwt) {
        ratingOnlyReviewUpdateCommandHandler.handle(new RatingOnlyReviewUpdateCommand(
                id,
                updateReviewDTO.rating(),
                JwtUtil.extractUserId(jwt)
        ));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete rating only review", description = "deleting rating only review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Rating only review deleted"),
            @ApiResponse(responseCode = "404", description = "Rating only review for deleting not found"),
            @ApiResponse(responseCode = "503", description = "The deleting failed because the server is down")
    })
    public ResponseEntity<Void> deleteReview(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        ratingOnlyReviewDeleteCommandHandler.handle(new RatingOnlyReviewDeleteCommand(
                id,
                JwtUtil.extractUserId(jwt)));
        return ResponseEntity.noContent().build();
    }
}
