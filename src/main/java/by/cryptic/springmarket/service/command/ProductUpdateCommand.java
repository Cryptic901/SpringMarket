package by.cryptic.springmarket.service.command;

import by.cryptic.springmarket.model.write.Product;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for updating {@link Product}
 */
public record ProductUpdateCommand(
        UUID productId,
        String name,
        BigDecimal price,
        Integer quantity,
        String description,
        String image,
        UUID createdBy,
        UUID categoryId) implements Serializable {
}
