package by.cryptic.categoryservice.repository.read;

import by.cryptic.categoryservice.model.read.CategoryView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategoryViewRepository extends MongoRepository<CategoryView, UUID> {
}
