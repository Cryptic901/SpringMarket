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
    private UUID userId;
    private UUID productId;
    private static final String version = "1.0";
    @Builder.Default
    private String source = CartDeletedProductEvent.class.getName();

    public CartDeletedProductEvent(UUID userId, UUID productId) {
        this.userId = userId;
        this.productId = productId;
    }
}
