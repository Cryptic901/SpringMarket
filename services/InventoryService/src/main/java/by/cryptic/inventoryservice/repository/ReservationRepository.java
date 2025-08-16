package by.cryptic.inventoryservice.repository;

import by.cryptic.inventoryservice.model.Reservation;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    @Query("SELECT r FROM Reservation r WHERE" +
            " r.productId = :productId AND r.orderId = :orderId")
    Optional<Reservation> findByReservedByProductId(@Param("orderId") UUID orderId,
                                                    @Param("productId") UUID productId);

    List<Reservation> findByOrderId(UUID orderId);
}
