package by.cryptic.cartservice.repository.write;

import by.cryptic.cartservice.model.write.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CartProductRepository extends JpaRepository<CartProduct, UUID> {
}
