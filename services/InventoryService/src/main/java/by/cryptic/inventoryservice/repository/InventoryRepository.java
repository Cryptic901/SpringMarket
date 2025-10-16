package by.cryptic.inventoryservice.repository;

import by.cryptic.inventoryservice.model.Inventory;
import by.cryptic.inventoryservice.model.Warehouse;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    void deleteByProductId(UUID productId);

    @Query("SELECT i FROM Inventory i WHERE i.productId = :productId AND" +
            " i.availableQuantity > :quantityToReserve AND i.warehouse = :warehouse")
    Optional<Inventory> findAvailableByProductId(@Param("productId") UUID productId,
                                                 @Param("quantityToReserve") int quantityToReserve,
                                                 @Param("warehouse") Warehouse warehouse);

    Optional<Inventory> findByProductId(UUID productId);

    @Query("SELECT i.availableQuantity FROM Inventory i WHERE i.productId = :productId")
    Integer getQuantityByProductId(@Param("productId") UUID productId);
}
