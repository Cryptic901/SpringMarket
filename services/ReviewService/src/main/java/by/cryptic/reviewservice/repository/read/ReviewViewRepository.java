package by.cryptic.reviewservice.repository.read;

import by.cryptic.reviewservice.model.read.ReviewView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReviewViewRepository extends MongoRepository<ReviewView, UUID> {
}
