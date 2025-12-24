package by.cryptic.inventoryservice.repository;

import by.cryptic.inventoryservice.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {

    @Query(value = """
            SELECT w.*
            FROM inventory_schema.warehouse w
            JOIN inventory_schema.inventory i on w.id = i.warehouse_id
            WHERE w.is_active = true
                        AND i.product_id = :productId
                        AND i.available_quantity >= :quantity
            ORDER BY w.location <-> ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography
            LIMIT :limit
            """, nativeQuery = true)
    List<Warehouse> findClosestWarehousesWithEnoughProducts(@Param("lat") double lat,
                                                            @Param("lon") double lon,
                                                            @Param("limit") int limit,
                                                            @Param("quantity") int quantity,
                                                            @Param("productId") UUID productId);

    @Query(value = """
            SELECT w.*
            FROM inventory_schema.warehouse w
            WHERE w.is_active = true AND :quantity + w.current_load <= w.capacity
            ORDER BY w.location <-> ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography
            LIMIT 1
            """, nativeQuery = true)
    Warehouse findClosestWarehousesWithEnoughSpace(@Param("lat") double lat,
                                                   @Param("lon") double lon,
                                                   @Param("quantity") int quantity);

    @Query("SELECT CASE WHEN COUNT(*) > 0 " +
            "THEN true " +
            "ELSE false END FROM Warehouse WHERE isActive = true AND capacity >= :quantity")
    Boolean existsWarehouseWithCapacity(@Param("quantity") int quantity);
}
