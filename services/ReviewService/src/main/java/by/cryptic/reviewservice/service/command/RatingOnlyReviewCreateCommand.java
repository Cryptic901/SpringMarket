package by.cryptic.reviewservice.service.command;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RatingOnlyReviewCreateCommand(
        @NotNull(message = "Rating should not be null")
        Double rating,
        @NotNull(message = "Product id should not be null")
        UUID productId,
        UUID userId) {
}
