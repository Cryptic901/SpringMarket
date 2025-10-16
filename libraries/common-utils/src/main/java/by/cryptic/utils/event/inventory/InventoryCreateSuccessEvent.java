package by.cryptic.utils.event.inventory;

import by.cryptic.utils.enums.ProductStatus;
import by.cryptic.utils.event.DomainEvent;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryCreateSuccessEvent extends DomainEvent {
    private String name;
    private String description;
    private int quantity;
    private BigDecimal price;
    private String image;
    private UUID categoryId;
    private ProductStatus productStatus = ProductStatus.ACTIVE;

    private static final String version = "1.0";
    @Builder.Default
    private String source = InventoryCreateSuccessEvent.class.getName();
}
