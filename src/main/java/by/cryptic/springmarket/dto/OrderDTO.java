package by.cryptic.springmarket.dto;

import by.cryptic.springmarket.enums.OrderStatus;
import by.cryptic.springmarket.enums.PaymentMethod;

import java.util.UUID;

public record OrderDTO(PaymentMethod paymentMethod, UUID productId, String location, OrderStatus status) {
}
