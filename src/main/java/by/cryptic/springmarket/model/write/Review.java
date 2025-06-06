package by.cryptic.springmarket.model.write;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
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
@Table(name = "reviews", schema = "public")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @JoinColumn(name = "product_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Product product;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Double rating;

    private String description;

    private String image;

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(insertable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(nullable = false, updatable = false, name = "created_by")
    private UUID createdBy;

    @LastModifiedBy
    @Column(insertable = false, name = "updated_by")
    private UUID updatedBy;


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return Objects.equals(id, review.id) &&
                Objects.equals(title, review.title) &&
                Objects.equals(rating, review.rating) &&
                Objects.equals(description, review.description) &&
                Objects.equals(image, review.image) &&
                Objects.equals(createdAt, review.createdAt) &&
                Objects.equals(updatedAt, review.updatedAt) &&
                Objects.equals(createdBy, review.createdBy) &&
                Objects.equals(updatedBy, review.updatedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, rating, description, image, createdAt, updatedAt, createdBy, updatedBy);
    }
}
