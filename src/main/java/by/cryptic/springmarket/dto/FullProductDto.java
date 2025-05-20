package by.cryptic.springmarket.dto;

import by.cryptic.springmarket.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO with all fields for {@link Product}
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FullProductDto implements Serializable {
   private String name;
   private BigDecimal price;
   private Integer quantity;
   private String description;
   private String image;
   private ShortCategoryDTO category;
   private UUID createdBy;
}