package by.cryptic.utils.event.cart;

import by.cryptic.utils.DTO.OrderedProductDTO;
import by.cryptic.utils.enums.PaymentMethod;
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
public class CartClearedSuccessEvent extends DomainEvent implements CartEvent {
    private UUID userId;
    private UUID orderId;
    private String userEmail;
    private PaymentMethod paymentMethod;
    private List<OrderedProductDTO> listOfProducts;
    private BigDecimal price;
    private static final String version = "1.0";
    @Builder.Default
    private String source = CartClearedByUserEvent.class.getName();
}
