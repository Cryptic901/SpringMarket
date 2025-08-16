package by.cryptic.utils.event.order;

import by.cryptic.utils.OrderStatus;
import by.cryptic.utils.event.DomainEvent;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCanceledEvent extends DomainEvent implements OrderEvent {

    private UUID orderId;
    private String userEmail;
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.CANCELLED;
    private String cancelReason;
    private static final String version = "1.0";
    @Builder.Default
    private String source = OrderCanceledEvent.class.getName();

    public OrderCanceledEvent(UUID id, String userEmail) {
        this.orderId = id;
        this.orderStatus = OrderStatus.CANCELLED;
        this.userEmail = userEmail;
    }
}
