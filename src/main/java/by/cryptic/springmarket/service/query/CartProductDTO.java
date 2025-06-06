package by.cryptic.springmarket.service.query;

import java.math.BigDecimal;
import java.util.UUID;

public record CartProductDTO(UUID productId, Integer quantity, BigDecimal pricePerUnit) {
}
