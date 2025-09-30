package by.cryptic.utils.event.cart;

import by.cryptic.utils.event.DomainEvent;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartClearedFailedEvent extends DomainEvent implements CartEvent {
    private UUID userId;
    private UUID orderId;
    private String userEmail;
    private static final String version = "1.0";
    @Builder.Default
    private String source = CartClearedByUserEvent.class.getName();
}
