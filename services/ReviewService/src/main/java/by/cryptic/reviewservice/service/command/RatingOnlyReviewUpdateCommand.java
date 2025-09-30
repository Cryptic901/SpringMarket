package by.cryptic.reviewservice.service.command;

import java.util.UUID;

public record RatingOnlyReviewUpdateCommand(
        UUID reviewId,
        Double rating,
        UUID userId) {
}

