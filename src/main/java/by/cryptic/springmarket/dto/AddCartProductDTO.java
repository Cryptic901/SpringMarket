package by.cryptic.springmarket.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record AddCartProductDTO(UUID id, Integer quantity, BigDecimal price) {
}
