package by.cryptic.utils.event.order;

import by.cryptic.utils.DTO.OrderedProductDTO;
import by.cryptic.utils.OrderStatus;
import by.cryptic.utils.event.DomainEvent;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCreatedEvent extends DomainEvent implements OrderEvent {
    private UUID orderId;
    private String userEmail;
    private BigDecimal price;
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.IN_PROGRESS;
    private List<OrderedProductDTO> listOfProducts;
    private String location;
    private UUID createdBy;
    private static final String version = "1.0";
    @Builder.Default
    private String source = OrderSuccessEvent.class.getName();
}
