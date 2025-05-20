package by.cryptic.springmarket.dto;

import by.cryptic.springmarket.enums.OrderStatus;

public record OrderResponse(OrderStatus orderStatus) {
}
