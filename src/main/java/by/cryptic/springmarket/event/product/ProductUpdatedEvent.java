package by.cryptic.springmarket.event.product;

import by.cryptic.springmarket.event.DomainEvent;
import by.cryptic.springmarket.model.write.Category;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductUpdatedEvent extends DomainEvent implements ProductEvent {
    private UUID productId;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private String image;
    private Category category;
}
