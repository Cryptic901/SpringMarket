package by.cryptic.utils.event.inventory;

import by.cryptic.utils.event.DomainEvent;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockReservationFailedEvent extends DomainEvent implements StockEvent {
    private UUID orderId;
    private String userEmail;
    private static final String version = "1.0";
    @Builder.Default
    private String source = StockReservedEvent.class.getName();
}
