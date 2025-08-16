package by.cryptic.utils.event.product;

import by.cryptic.utils.event.DomainEvent;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductUpdatedQuantityFromStockEvent extends DomainEvent implements ProductEvent {
    private UUID productId;
    private Integer quantity;
    private static final String version = "1.0";
    @Builder.Default
    private String source = ProductUpdatedQuantityFromStockEvent.class.getName();
}
