package by.cryptic.springmarket.service.command;

import by.cryptic.springmarket.enums.PaymentMethod;

import java.util.UUID;

public record OrderUpdateCommand(UUID orderId, String location, PaymentMethod paymentMethod) {
}
