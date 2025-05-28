package by.cryptic.springmarket.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CartProductDTO(UUID productId, Integer quantity, BigDecimal pricePerUnit) {
}
