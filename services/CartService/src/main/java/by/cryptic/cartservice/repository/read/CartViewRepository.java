package by.cryptic.cartservice.repository.read;

import by.cryptic.cartservice.model.read.CartView;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartViewRepository extends MongoRepository<CartView, UUID> {

    Optional<CartView> findCartViewByUserId(UUID userId);
}
