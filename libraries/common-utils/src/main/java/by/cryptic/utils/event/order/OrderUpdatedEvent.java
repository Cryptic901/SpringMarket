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
public class OrderUpdatedEvent extends DomainEvent implements OrderEvent {

    private UUID orderId;
    private String userEmail;
    private OrderStatus status;
    private LocalDateTime updatedTimestamp;
}
