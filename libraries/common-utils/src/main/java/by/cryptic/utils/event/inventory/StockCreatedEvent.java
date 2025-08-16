package by.cryptic.utils.event.inventory;

import by.cryptic.utils.DTO.OrderedProductDTO;
import by.cryptic.utils.OrderStatus;
import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.order.OrderCreatedEvent;
import by.cryptic.utils.event.order.OrderEvent;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockCreatedEvent extends DomainEvent implements StockEvent {
    private UUID orderId;
    private String userEmail;
    private BigDecimal price;
    private List<OrderedProductDTO> listOfProducts;
    private UUID createdBy;
    private static final String version = "1.0";
    @Builder.Default
    private String source = StockCreatedEvent.class.getName();
}
