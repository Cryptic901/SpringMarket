package by.cryptic.utils.event.order;

import by.cryptic.utils.event.DomainEvent;
import lombok.*;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinalizeOrderEvent extends DomainEvent implements OrderEvent {
    private UUID orderId;
    private UUID paymentId;
    private static final String version = "1.0";
    @Builder.Default
    private String source = FinalizeOrderEvent.class.getName();
}
