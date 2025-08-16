package by.cryptic.productservice.model.read;

import by.cryptic.utils.ProductStatus;
import jakarta.persistence.Column;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Document(collection = "product_view")
public class ProductView {

    @MongoId
    private UUID productId;

    @Column(name = "category_id")
    private UUID categoryId;

    private String name;

    private ProductStatus productStatus;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal price;

    private Integer quantity;

    private String description;

    private String image;

    @Column(name = "created_by")
    private UUID createdBy;


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductView that = (ProductView) o;
        return Objects.equals(productId, that.productId) && Objects.equals(categoryId, that.categoryId) && Objects.equals(name, that.name) && Objects.equals(price, that.price) && Objects.equals(quantity, that.quantity) && Objects.equals(description, that.description) && Objects.equals(image, that.image) && Objects.equals(createdBy, that.createdBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, categoryId, name, price, quantity, description, image, createdBy);
    }
}