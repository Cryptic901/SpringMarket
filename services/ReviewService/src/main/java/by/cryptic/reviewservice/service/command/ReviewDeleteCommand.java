package by.cryptic.reviewservice.service.command;

import java.util.UUID;

public record ReviewDeleteCommand(UUID reviewId, UUID userId) {
}
