package by.cryptic.utils.event.product;

import by.cryptic.utils.event.DomainEvent;
import lombok.*;

import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDeletedEvent extends DomainEvent implements ProductEvent {
    private UUID productId;
    private static final String version = "1.0";
    @Builder.Default
    private String source = ProductDeletedEvent.class.getName();

    public ProductDeletedEvent(UUID productId) {
        this.productId = productId;
    }
}