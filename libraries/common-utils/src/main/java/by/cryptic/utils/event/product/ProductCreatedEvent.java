package by.cryptic.utils.event.product;

import by.cryptic.utils.event.DomainEvent;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductCreatedEvent extends DomainEvent implements ProductEvent {
    private UUID productId;
    private String name;
    private BigDecimal price;
    private String description;
    private Integer quantity;
    private String image;
    private UUID categoryId;
    private UUID createdBy;
}