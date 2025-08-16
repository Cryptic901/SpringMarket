package by.cryptic.productservice.service.command;


import by.cryptic.utils.ProductStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record ProductUpdateCommand(
        UUID productId,
        String name,
        BigDecimal price,
        Integer quantity,
        String description,
        String image,
        UUID createdBy,
        UUID categoryId,
        ProductStatus productStatus) implements Serializable {
}
