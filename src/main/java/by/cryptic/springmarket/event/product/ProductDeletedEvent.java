package by.cryptic.springmarket.event.product;

import by.cryptic.springmarket.event.DomainEvent;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDeletedEvent extends DomainEvent implements ProductEvent {
    private UUID productId;
}
