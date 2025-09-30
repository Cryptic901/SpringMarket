package by.cryptic.utils.DTO;

import by.cryptic.utils.enums.PaymentMethod;
import by.cryptic.utils.enums.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentDTO(PaymentMethod paymentMethod, PaymentStatus paymentStatus,
                         UUID orderId, BigDecimal price) {
}
