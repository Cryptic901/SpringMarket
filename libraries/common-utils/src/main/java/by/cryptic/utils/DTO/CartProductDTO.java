package by.cryptic.utils.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartProductDTO {
    private UUID productId;
    private Integer quantity;
    private BigDecimal pricePerUnit;
}
