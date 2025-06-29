package by.cryptic.reviewservice.service.command;

import java.util.UUID;

public record ReviewUpdateCommand(
        UUID reviewId,
        String title,
        Double rating,
        String description,
        String image,
        UUID userId) {
}
