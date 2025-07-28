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
@Document(collection = "review_view")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewView {

    @MongoId
    private UUID reviewId;

    private UUID productId;

    private String title;

    private Double rating;

    private String description;

    private String image;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private UUID createdBy;

    private UUID updatedBy;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ReviewView that = (ReviewView) o;
        return Objects.equals(reviewId, that.reviewId) &&
                Objects.equals(productId, that.productId) &&
                Objects.equals(title, that.title) &&
                Objects.equals(rating, that.rating) &&
                Objects.equals(description, that.description) &&
                Objects.equals(createdBy, that.createdBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reviewId, productId, title, rating, description, createdBy);
    }
}
