package by.cryptic.reviewservice.repository.read;

import by.cryptic.reviewservice.model.read.RatingOnlyReviewView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RatingOnlyReviewViewRepository extends MongoRepository<RatingOnlyReviewView, UUID> {
}
