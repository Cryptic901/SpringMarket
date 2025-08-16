package by.cryptic.inventoryservice.repository;

import by.cryptic.inventoryservice.model.Inventory;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    void deleteByProductId(UUID productId);

    @Query("SELECT i FROM Inventory i WHERE i.productId = :productId AND i.warehouseId = :warehouseId")
    Optional<Inventory> findByProductIdAndWarehouseId(@Param("productId") UUID productId,
                                                      @Param("warehouseId") UUID warehouseId);

    @Query("SELECT i FROM Inventory i WHERE i.productId = :productId AND" +
            " i.availableQuantity > 0")
    List<Inventory> findAvailableByProductId(@Param("productId") UUID productId);

    @Query("SELECT i FROM Inventory i WHERE i.reservedQuantity > 0")
    List<Inventory> findByReservedByProductId(@Param("productId") UUID productId);
}
