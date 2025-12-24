package by.cryptic.reviewservice.service.command;

import java.util.UUID;

public record RatingOnlyReviewDeleteCommand(UUID reviewId, UUID userId) {
}
