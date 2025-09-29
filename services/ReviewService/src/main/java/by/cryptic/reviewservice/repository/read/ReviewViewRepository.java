package by.cryptic.reviewservice.repository.read;

import by.cryptic.reviewservice.model.read.ReviewView;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface ReviewViewRepository extends MongoRepository<ReviewView, UUID> {
    void deleteAllByProductId(UUID productId);

    @DeleteQuery("{ '_id': ?0 }")
    long deleteByProductIdReturningCount(UUID productId);
}
