package by.cryptic.springmarket.event.cart;

import by.cryptic.springmarket.event.DomainEvent;
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
