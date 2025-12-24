package by.cryptic.orderservice.service.command;

import by.cryptic.utils.enums.PaymentMethod;

import java.util.UUID;

public record OrderCreateCommand(Double lat, Double lon, PaymentMethod paymentMethod,
                                 UUID userId, String userEmail,
                                 Integer warehouseLimit) {
}
