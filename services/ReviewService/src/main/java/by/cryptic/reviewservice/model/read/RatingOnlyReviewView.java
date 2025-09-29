package by.cryptic.reviewservice.model.read;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@Document(collection = "rating_only_review_view")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingOnlyReviewView {

    @MongoId
    private UUID reviewId;

    private UUID productId;

    private Double rating;

    private LocalDateTime createdAt;

    private UUID createdBy;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RatingOnlyReviewView that = (RatingOnlyReviewView) o;
        return Objects.equals(reviewId, that.reviewId) && Objects.equals(productId, that.productId) && Objects.equals(rating, that.rating) && Objects.equals(createdAt, that.createdAt) && Objects.equals(createdBy, that.createdBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reviewId, productId, rating, createdAt, createdBy);
    }
}
