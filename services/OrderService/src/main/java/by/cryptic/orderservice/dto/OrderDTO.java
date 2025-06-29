package by.cryptic.orderservice.dto;

import by.cryptic.utils.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO implements Serializable {
    private String location;
    private OrderStatus orderStatus;
    private BigDecimal price;
    private UUID createdBy;
    private UUID updatedBy;
}
