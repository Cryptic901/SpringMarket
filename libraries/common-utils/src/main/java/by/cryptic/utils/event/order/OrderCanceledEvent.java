package by.cryptic.utils.event.order;

import by.cryptic.utils.enums.OrderStatus;
import by.cryptic.utils.event.DomainEvent;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCanceledEvent extends DomainEvent implements OrderEvent {

    private UUID orderId;
    private String userEmail;
    private UUID userId;
    private BigDecimal price;
    private String location;
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.CANCELLED;
    private String cancelReason;
    private static final String version = "1.0";
    @Builder.Default
    private String source = OrderCanceledEvent.class.getName();
}
