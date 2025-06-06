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
public class OrderCreatedEvent extends DomainEvent implements OrderEvent {
    private UUID orderId;
    private String userEmail;
    private OrderStatus status = OrderStatus.IN_PROGRESS;
    private PaymentMethod paymentMethod;
    private LocalDateTime createdTimestamp;
    private String location;
    private UUID createdBy;
}
