package by.cryptic.orderservice.repository.write;

import by.cryptic.orderservice.model.write.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderProductRepository extends JpaRepository<OrderProduct, UUID> {
}
