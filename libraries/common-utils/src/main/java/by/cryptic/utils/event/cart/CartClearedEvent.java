package by.cryptic.utils.event.cart;

import by.cryptic.utils.event.DomainEvent;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartClearedEvent extends DomainEvent implements CartEvent {
    private UUID cartId;
    private static final String version = "1.0";
    @Builder.Default
    private String source = CartClearedEvent.class.getName();

    public CartClearedEvent(UUID cartId) {
        this.cartId = cartId;
    }
}
