package by.cryptic.reviewservice.model.write;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "rating_only_review", schema = "review_schema")
public class RatingOnlyReview {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "product_id")
    private UUID productId;

    @Column(nullable = false)
    private Double rating;

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(nullable = false, updatable = false, name = "created_by")
    private UUID createdBy;


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RatingOnlyReview that = (RatingOnlyReview) o;
        return Objects.equals(id, that.id) && Objects.equals(productId, that.productId) && Objects.equals(rating, that.rating) && Objects.equals(createdAt, that.createdAt) && Objects.equals(createdBy, that.createdBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, productId, rating, createdAt, createdBy);
    }
}
