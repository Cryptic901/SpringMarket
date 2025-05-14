package by.cryptic.springmarket.dto;

import by.cryptic.springmarket.model.Category;
import by.cryptic.springmarket.model.Product;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for updating {@link Product}
 */
public record UpdateProductDTO(String name,
                               BigDecimal price,
                               Integer quantity,
                               String description,
                               String image,
                               UUID createdBy,
                               UUID categoryId) implements Serializable {
}
