package by.cryptic.springmarket.dto;

import by.cryptic.springmarket.enums.OrderStatus;
import by.cryptic.springmarket.enums.PaymentMethod;
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
    private PaymentMethod paymentMethod;
    private String location;
    private OrderStatus orderStatus;
    private BigDecimal price;
    private UUID createdBy;
    private UUID updatedBy;
}
