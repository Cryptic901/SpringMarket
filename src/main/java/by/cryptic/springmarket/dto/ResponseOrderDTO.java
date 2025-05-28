package by.cryptic.springmarket.dto;

import by.cryptic.springmarket.enums.OrderStatus;
import by.cryptic.springmarket.enums.PaymentMethod;

import java.util.List;
import java.util.UUID;

public record ResponseOrderDTO(PaymentMethod paymentMethod, List<UUID> productIDs, String location, OrderStatus orderStatus) {
}
