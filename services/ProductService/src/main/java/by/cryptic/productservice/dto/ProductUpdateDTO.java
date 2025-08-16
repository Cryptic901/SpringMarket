package by.cryptic.productservice.dto;

import by.cryptic.utils.ProductStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductUpdateDTO(String name,
                               BigDecimal price,
                               Integer quantity,
                               String description,
                               String image,
                               UUID categoryId,
                               ProductStatus productStatus) {
}
