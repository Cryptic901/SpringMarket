package by.cryptic.reviewservice.repository.write;

import by.cryptic.reviewservice.model.write.RatingOnlyReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RatingOnlyReviewRepository extends JpaRepository<RatingOnlyReview, UUID> {
}
