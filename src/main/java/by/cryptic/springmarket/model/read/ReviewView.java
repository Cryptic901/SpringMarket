package by.cryptic.springmarket.model.read;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "review_view", schema = "public")
public class ReviewView {

    @Id
    @Column(name = "reviewId", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID reviewId;

    @Column(name = "productId")
    private UUID productId;

    private String title;

    private Double rating;

    private String description;

    private String image;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;
}
