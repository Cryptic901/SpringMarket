package by.cryptic.springmarket.event.order;

import by.cryptic.springmarket.enums.OrderStatus;
import by.cryptic.springmarket.event.DomainEvent;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCanceledEvent extends DomainEvent implements OrderEvent {

    private UUID orderId;
    private String userEmail;
    private OrderStatus orderStatus = OrderStatus.CANCELLED;
    private LocalDateTime canceledTimestamp;

    public OrderCanceledEvent(UUID id, String userEmail) {
        this.orderId = id;
        this.canceledTimestamp = LocalDateTime.now();
        this.orderStatus = OrderStatus.CANCELLED;
        this.userEmail = userEmail;
    }
}
