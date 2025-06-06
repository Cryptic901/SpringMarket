package by.cryptic.springmarket.model.read;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@Builder
@Table(name = "cart_view", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
public class CartView {

    @Id
    @Column(name = "cartId", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID cartId;

    private UUID userId;

    private BigDecimal total = BigDecimal.ZERO;

    @Type(JsonBinaryType.class)
    private List<CartProductView> products = new ArrayList<>();
}
