package by.cryptic.reviewservice.repository.read;

import by.cryptic.reviewservice.model.read.ReviewView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReviewViewRepository extends JpaRepository<ReviewView, UUID> {
}
