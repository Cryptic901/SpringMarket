package by.cryptic.springmarket.service.command;

import java.util.UUID;

public record ReviewUpdateCommand(
        UUID reviewId,
        UUID productId,
        String title,
        Double rating,
        String description,
        String image) {
}
