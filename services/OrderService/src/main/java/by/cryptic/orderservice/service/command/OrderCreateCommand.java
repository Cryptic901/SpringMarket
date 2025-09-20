package by.cryptic.orderservice.service.command;

import by.cryptic.utils.enums.PaymentMethod;

import java.util.UUID;

public record OrderCreateCommand(String location, PaymentMethod paymentMethod,
                                 UUID userId, String userEmail) {
}
