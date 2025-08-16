package by.cryptic.utils.event.inventory;

import by.cryptic.utils.DTO.ReservedProductDTO;
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
public class StockReservedEvent extends DomainEvent implements StockEvent {
    private UUID orderId;
    private String userEmail;
    private BigDecimal orderPrice;
    private UUID userId;
    private List<ReservedProductDTO> products;
    private static final String version = "1.0";
    @Builder.Default
    private String source = StockReservedEvent.class.getName();
}
