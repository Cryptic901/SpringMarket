package by.cryptic.springmarket.event.product;

import by.cryptic.springmarket.event.DomainEvent;
import by.cryptic.springmarket.model.write.Category;
import by.cryptic.springmarket.model.write.Product;
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
    private Category category;
    private UUID createdBy;
}
