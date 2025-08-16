package by.cryptic.inventoryservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "reservation", schema = "inventory_schema")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "quantity_to_reserve")
    private Integer quantityToReserve;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "warehouse_id")
    private UUID warehouseId;


    public void addQuantity(int quantityToAdd) {
        quantityToReserve += quantityToAdd;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(id, that.id) && Objects.equals(productId, that.productId) && Objects.equals(orderId, that.orderId) && Objects.equals(warehouseId, that.warehouseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, productId, orderId, warehouseId);
    }
}
