package by.cryptic.utils.event.cart;

import by.cryptic.utils.event.DomainEvent;
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
}
