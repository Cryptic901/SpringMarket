package by.cryptic.utils.event.payment;

import by.cryptic.utils.PaymentMethod;
import by.cryptic.utils.PaymentStatus;
import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.order.OrderCanceledEvent;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class PaymentSuccessEvent extends DomainEvent implements PaymentEvent {

    private UUID paymentId;
    private PaymentMethod paymentMethod;
    private BigDecimal price;
    private UUID orderId;
    private UUID userId;
    private PaymentStatus paymentStatus;
    private static final String version = "1.0";
    @Builder.Default
    private String source = OrderCanceledEvent.class.getName();
}
