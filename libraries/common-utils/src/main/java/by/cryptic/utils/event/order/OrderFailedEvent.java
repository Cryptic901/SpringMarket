package by.cryptic.utils.event.order;

import by.cryptic.utils.DTO.OrderedProductDTO;
import by.cryptic.utils.enums.OrderStatus;
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
public class OrderFailedEvent extends DomainEvent implements OrderEvent {
    private UUID orderId;
    private String userEmail;
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.FAILED;
    private List<OrderedProductDTO> listOfProducts;
    private String failureReason;
    private static final String version = "1.0";
    @Builder.Default
    private String source = OrderFailedEvent.class.getName();
}
