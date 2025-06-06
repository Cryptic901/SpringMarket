package by.cryptic.springmarket.repository.write;

import by.cryptic.springmarket.model.write.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CartProductRepository extends JpaRepository<CartProduct, UUID> {

    @Query("SELECT cp FROM CartProduct cp JOIN FETCH cp.product WHERE cp.id IN :ids")
    List<CartProduct> findAllByIdWithProducts(@Param("ids") List<UUID> ids);
}
