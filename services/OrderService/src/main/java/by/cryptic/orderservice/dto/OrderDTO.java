package by.cryptic.orderservice.dto;

import by.cryptic.utils.enums.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {
    @NotBlank(message = "Longitude should be not blank")
    private double lon;
    @NotBlank(message = "Latitude should be not blank")
    private double lat;
    @NotNull(message = "Order status should be not null")
    private OrderStatus orderStatus;
    @NotNull(message = "Price should be not null")
    private BigDecimal price;
    @NotNull(message = "User id should be not null")
    private UUID createdBy;
    @NotNull(message = "Updated by id should be not null")
    private UUID updatedBy;
}
