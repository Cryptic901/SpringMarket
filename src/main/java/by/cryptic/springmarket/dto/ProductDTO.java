package by.cryptic.springmarket.dto;

import by.cryptic.springmarket.model.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link Product}
 */
public record ProductDTO(@NotBlank(message = "Product should have name")
                         String name,
                         @NotNull(message = "Product should have price")
                         BigDecimal price,
                         @NotNull(message = "Product should have quantity")
                         Integer quantity,
                         String description,
                         @NotBlank(message = "Product should have image")
                         String image,
                         ShortCategoryDTO category) implements Serializable {
}