package by.cryptic.utils.event.cart;

import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.order.OrderUpdatedEvent;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartDeletedProductEvent extends DomainEvent implements CartEvent {
    private UUID cartId;
    private UUID productId;
    private static final String version = "1.0";
    @Builder.Default
    private String source = CartDeletedProductEvent.class.getName();

    public CartDeletedProductEvent(UUID cartId, UUID productId) {
        this.cartId = cartId;
        this.productId = productId;
    }
}
