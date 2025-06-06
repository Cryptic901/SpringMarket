package by.cryptic.springmarket.model.read;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Table(name = "product_view", schema = "public")
@Entity
public class ProductView {

    @Id
    @Column(name = "productId", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID productId;

    private String name;

    private BigDecimal price;

    private Integer quantity;

    private String description;

    private String image;

    @Column(name = "created_by")
    private UUID createdBy;
}
