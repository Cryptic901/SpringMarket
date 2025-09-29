package by.cryptic.productservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductCreateDTO(@NotBlank(message = "Product should have name")
                               String name,
                               @NotNull(message = "Product should have price")
                               BigDecimal price,
                               @NotNull(message = "Product should have quantity")
                               Integer quantity,
                               @JsonProperty(defaultValue = "No description")
                               String description,
                               @NotBlank(message = "Product should have image")
                               String image,
                               @NotNull(message = "Product should have category")
                               UUID categoryId) {
}
