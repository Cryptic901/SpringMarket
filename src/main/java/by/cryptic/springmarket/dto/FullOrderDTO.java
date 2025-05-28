package by.cryptic.springmarket.dto;

import by.cryptic.springmarket.enums.OrderStatus;
import by.cryptic.springmarket.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FullOrderDTO implements Serializable {
    private PaymentMethod paymentMethod;
    private List<ProductDTO> products;
    private String location;
    private OrderStatus status = OrderStatus.IN_STOCK;
    private UUID createdBy;
    private UUID updatedBy;
}
