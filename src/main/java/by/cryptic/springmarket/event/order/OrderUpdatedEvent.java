package by.cryptic.springmarket.event.order;

import by.cryptic.springmarket.enums.OrderStatus;
import by.cryptic.springmarket.enums.PaymentMethod;
import by.cryptic.springmarket.event.DomainEvent;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderUpdatedEvent extends DomainEvent implements OrderEvent {

    private UUID orderId;
    private String userEmail;
    private OrderStatus status;
    private PaymentMethod paymentMethod;
    private LocalDateTime updatedTimestamp;
}
