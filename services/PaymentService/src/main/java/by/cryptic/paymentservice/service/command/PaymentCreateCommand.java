package by.cryptic.paymentservice.service.command;

import by.cryptic.utils.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentCreateCommand(PaymentMethod paymentMethod,
                                   UUID userId, UUID orderId, String email,
                                   BigDecimal price, LocalDateTime timestamp) {
}
