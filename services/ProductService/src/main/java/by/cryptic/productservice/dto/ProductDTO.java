package by.cryptic.productservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record ProductDTO(@NotBlank(message = "Product should have name")
                         String name,
                         @NotNull(message = "Product should have price")
                         BigDecimal price,
                         @NotNull(message = "Product should have quantity")
                         Integer quantity,
                         String description,
                         @NotBlank(message = "Product should have image")
                         String image,
                         UUID categoryId,
                         UUID createdBy) implements Serializable {
}