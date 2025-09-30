package by.cryptic.cartservice.model.read;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "cart_view")
public class CartView {

    @MongoId
    private UUID cartId;

    private UUID userId;

    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    @Builder.Default
    private List<CartProductView> products = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CartView cartView = (CartView) o;
        return Objects.equals(cartId, cartView.cartId) &&
                Objects.equals(userId, cartView.userId) &&
                Objects.equals(total, cartView.total);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cartId, userId, total);
    }
}
