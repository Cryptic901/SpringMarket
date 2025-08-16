package by.cryptic.inventoryservice.model;

import by.cryptic.exceptions.OutOfStockException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
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
@Table(name = "inventory", schema = "inventory_schema", indexes = {
        @Index(name = "idx_product_warehouse", columnList = "product_id, warehouse_id"),
})
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "warehouse_id")
    private UUID warehouseId;

    @Column(name = "available_quantity")
    private Integer availableQuantity;

    public boolean canReserve(int quantityToReserve) {
        return availableQuantity >= quantityToReserve;
    }

    public void reserve(int quantityToReserve) {
        if (!canReserve(quantityToReserve)) {
            throw new OutOfStockException("There are not enough products to reserve it");
        }
        availableQuantity -= quantityToReserve;
    }

    public void returnToStock(int quantityToReturn) {
        availableQuantity += quantityToReturn;
    }

    public void confirmFromReserve(int quantityToRelease) {
        availableQuantity -= quantityToRelease;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Inventory inventory = (Inventory) o;
        return Objects.equals(id, inventory.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
