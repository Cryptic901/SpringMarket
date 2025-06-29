package by.cryptic.productservice.repository.write;

import by.cryptic.productservice.model.write.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findById(UUID id);
    void deleteById(UUID id);
}
