package by.cryptic.springmarket.repository.read;

import by.cryptic.springmarket.model.read.ProductView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductViewRepository extends JpaRepository<ProductView, UUID>, JpaSpecificationExecutor<ProductView> {
}
