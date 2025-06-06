package by.cryptic.springmarket.repository.read;

import by.cryptic.springmarket.model.read.ReviewView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReviewViewRepository extends JpaRepository<ReviewView, UUID> {
}
