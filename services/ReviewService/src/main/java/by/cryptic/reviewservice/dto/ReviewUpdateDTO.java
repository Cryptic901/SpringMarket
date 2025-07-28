package by.cryptic.reviewservice.dto;

import java.util.UUID;

public record ReviewUpdateDTO(
        String title,
        Double rating,
        String description,
        String image) {
}
