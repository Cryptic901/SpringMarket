package by.cryptic.springmarket.model.read;

import by.cryptic.springmarket.model.write.Product;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@Builder
@Table(name = "category_view", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
public class CategoryView {

    @Id
    @Column(name = "categoryId", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID categoryId;
    private String name;
    private String description;
}
