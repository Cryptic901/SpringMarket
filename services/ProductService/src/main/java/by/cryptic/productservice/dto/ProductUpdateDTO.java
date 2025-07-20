package by.cryptic.productservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductUpdateDTO(String name,
                               BigDecimal price,
                               Integer quantity,
                               String description,
                               String image,
                               UUID categoryId) {
}
