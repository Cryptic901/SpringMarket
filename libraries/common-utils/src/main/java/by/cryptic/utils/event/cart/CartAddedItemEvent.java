package by.cryptic.utils.event.cart;

import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.order.OrderUpdatedEvent;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartAddedItemEvent extends DomainEvent implements CartEvent {

    private UUID cartId;
    private UUID productId;
    private UUID userId;
    private BigDecimal price;
    private static final String version = "1.0";
    @Builder.Default
    private String source = CartAddedItemEvent.class.getName();
}
