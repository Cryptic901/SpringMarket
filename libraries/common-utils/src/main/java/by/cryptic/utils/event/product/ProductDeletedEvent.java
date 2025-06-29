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
}