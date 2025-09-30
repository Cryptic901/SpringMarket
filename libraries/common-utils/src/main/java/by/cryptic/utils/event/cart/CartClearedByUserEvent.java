package by.cryptic.utils.event.cart;

import by.cryptic.utils.event.DomainEvent;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartClearedByUserEvent extends DomainEvent implements CartEvent {
    private UUID userId;
    private static final String version = "1.0";
    @Builder.Default
    private String source = CartClearedByUserEvent.class.getName();

}
