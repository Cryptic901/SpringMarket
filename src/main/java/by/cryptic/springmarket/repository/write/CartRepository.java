package by.cryptic.springmarket.repository.write;

import by.cryptic.springmarket.model.write.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {

    @Query("SELECT c FROM Cart c left join fetch c.items WHERE c.user.id = :id")
    Optional<Cart> findByUserIdWithItems(@Param("id") UUID id);
}
