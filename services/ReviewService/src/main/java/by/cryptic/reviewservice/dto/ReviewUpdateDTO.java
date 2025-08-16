package by.cryptic.reviewservice.dto;

public record ReviewUpdateDTO(
        String title,
        Double rating,
        String description,
        String image) {
}
