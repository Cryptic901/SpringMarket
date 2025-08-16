package by.cryptic.utils.event.product;

import by.cryptic.utils.ProductStatus;
import by.cryptic.utils.event.DomainEvent;
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
    private UUID categoryId;
    private ProductStatus productStatus;
    private static final String version = "1.0";
    @Builder.Default
    private String source = ProductUpdatedEvent.class.getName();
}