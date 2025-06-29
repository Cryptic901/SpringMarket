package by.cryptic.utils.event.order;

import by.cryptic.utils.OrderStatus;
import by.cryptic.utils.event.DomainEvent;
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
