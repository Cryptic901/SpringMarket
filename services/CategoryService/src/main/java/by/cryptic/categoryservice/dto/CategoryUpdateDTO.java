package by.cryptic.categoryservice.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryUpdateDTO(
        String name,
        String description) {
}
