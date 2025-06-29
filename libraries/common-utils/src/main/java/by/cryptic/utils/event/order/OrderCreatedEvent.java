package by.cryptic.utils.event.order;

import by.cryptic.utils.OrderStatus;
import by.cryptic.utils.event.DomainEvent;
import lombok.*;

import java.math.BigDecimal;
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
    private BigDecimal price;
    private OrderStatus orderStatus = OrderStatus.IN_PROGRESS;
    private LocalDateTime createdTimestamp;
    private String location;
    private UUID createdBy;
}
