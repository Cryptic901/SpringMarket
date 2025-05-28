package by.cryptic.springmarket.dto;

import by.cryptic.springmarket.model.CartProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartDTO(UUID id, BigDecimal total, List<CartProduct> products) {
}
