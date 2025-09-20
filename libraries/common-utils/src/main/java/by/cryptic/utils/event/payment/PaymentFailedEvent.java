package by.cryptic.utils.event.payment;

import by.cryptic.utils.enums.PaymentMethod;
import by.cryptic.utils.enums.PaymentStatus;
import by.cryptic.utils.event.DomainEvent;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentFailedEvent extends DomainEvent implements PaymentEvent {

    private UUID paymentId;
    private PaymentMethod paymentMethod;
    private BigDecimal price;
    private UUID orderId;
    private UUID userId;
    private PaymentStatus paymentStatus = PaymentStatus.FAILED;
    private String email;
    private static final String version = "1.0";
    @Builder.Default
    private String source = PaymentFailedEvent.class.getName();
}
