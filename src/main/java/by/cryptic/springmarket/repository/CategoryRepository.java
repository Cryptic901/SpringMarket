package by.cryptic.springmarket.repository;

import by.cryptic.springmarket.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findById(UUID id);
    void deleteById(UUID id);
}
