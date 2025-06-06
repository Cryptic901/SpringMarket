package by.cryptic.springmarket.repository.read;

import by.cryptic.springmarket.model.read.CartView;
import by.cryptic.springmarket.model.write.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartViewRepository extends JpaRepository<CartView, UUID> {


    @Query("SELECT c FROM CartView c left join fetch c.products WHERE c.userId = :id")
    Optional<Cart> findByUserIdWithItems(@Param("id") UUID id);
}
