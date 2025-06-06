package by.cryptic.springmarket.model.read;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartProductView {
    private UUID productId;
    private Integer quantity;
    private BigDecimal price;
}


