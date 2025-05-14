package by.cryptic.springmarket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateProductDTO(@NotBlank(message = "Product should have name")
                               String name,
                               @NotNull(message = "Product should have price")
                               BigDecimal price,
                               @NotNull(message = "Product should have quantity")
                               Integer quantity,
                               String description,
                               @NotBlank(message = "Product should have image")
                               String image,
                               UUID categoryId) {
}
