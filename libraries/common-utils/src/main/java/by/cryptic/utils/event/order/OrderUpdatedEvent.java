package by.cryptic.utils.event.order;

import by.cryptic.utils.OrderStatus;
import by.cryptic.utils.event.DomainEvent;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;

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
    private static final String version = "1.0";
    @Builder.Default
    private String source = OrderUpdatedEvent.class.getName();
}
