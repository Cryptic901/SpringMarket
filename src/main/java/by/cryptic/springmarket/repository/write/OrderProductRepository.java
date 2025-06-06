package by.cryptic.springmarket.repository.write;

import by.cryptic.springmarket.model.write.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderProductRepository extends JpaRepository<OrderProduct, UUID> {
}
