package by.cryptic.reviewservice.dto;

import java.util.UUID;

public record ReviewUpdateDTO(
        UUID reviewId,
        String title,
        Double rating,
        String description,
        String image) {
}
