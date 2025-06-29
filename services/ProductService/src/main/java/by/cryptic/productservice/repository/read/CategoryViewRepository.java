package by.cryptic.productservice.repository.read;

import by.cryptic.productservice.model.read.CategoryView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategoryViewRepository extends JpaRepository<CategoryView, UUID> {
}
