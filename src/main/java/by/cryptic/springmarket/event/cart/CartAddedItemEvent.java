package by.cryptic.springmarket.event.cart;

import by.cryptic.springmarket.event.DomainEvent;
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
    private BigDecimal price;
}
