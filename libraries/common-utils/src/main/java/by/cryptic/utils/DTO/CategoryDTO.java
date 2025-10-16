package by.cryptic.utils.DTO;

import jakarta.validation.constraints.NotBlank;

public record CategoryDTO(
        @NotBlank(message = "Name should be not null")
        String name,
        @NotBlank(message = "Description should be not null")
        String description) {
}
