package by.cryptic.utils.DTO;

import by.cryptic.utils.PaymentMethod;
import by.cryptic.utils.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentDTO(PaymentMethod paymentMethod, PaymentStatus paymentStatus,
                         UUID orderId, BigDecimal price) {
}
