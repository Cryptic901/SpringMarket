package by.cryptic.springmarket.repository;

import by.cryptic.springmarket.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findById(UUID id);
    void deleteById(UUID id);
}
