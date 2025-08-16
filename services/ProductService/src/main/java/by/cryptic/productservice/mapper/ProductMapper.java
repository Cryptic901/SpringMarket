package by.cryptic.productservice.mapper;

import by.cryptic.productservice.model.read.ProductView;
import by.cryptic.productservice.model.write.Product;
import by.cryptic.productservice.service.command.ProductUpdateCommand;
import by.cryptic.utils.DTO.OrderedProductDTO;
import by.cryptic.utils.DTO.ProductDTO;
import by.cryptic.utils.event.product.ProductUpdatedEvent;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public static ProductDTO toDto(ProductView product) {
        if (product == null) {
            return null;
        }
        return new ProductDTO(product.getName(),
                product.getPrice(),
                product.getQuantity(),
                product.getDescription(),
                product.getImage(),
                product.getCategoryId());
    }

    public static ProductDTO toDto(Product product) {
        if (product == null) {
            return null;
        }
        return new ProductDTO(product.getName(),
                product.getPrice(),
                product.getQuantity(),
                product.getDescription(),
                product.getImage(),
                product.getCategoryId());
    }

    public static void updateEntity(Product product, ProductUpdateCommand updateProductDTO) {
        if (product == null || updateProductDTO == null) return;

        if (updateProductDTO.productId() != null) {
            product.setId(updateProductDTO.productId());
        }
        if (updateProductDTO.name() != null) {
            product.setName(updateProductDTO.name());
        }
        if (updateProductDTO.productStatus() != null) {
            product.setProductStatus(updateProductDTO.productStatus());
        }
        if (updateProductDTO.price() != null) {
            product.setPrice(updateProductDTO.price());
        }
        if (updateProductDTO.quantity() != null) {
            product.setQuantity(updateProductDTO.quantity());
        }
        if (updateProductDTO.description() != null) {
            product.setDescription(updateProductDTO.description());
        }
        if (updateProductDTO.image() != null) {
            product.setImage(updateProductDTO.image());
        }
        if (updateProductDTO.createdBy() != null) {
            product.setCreatedBy(updateProductDTO.createdBy());
        }
        if (updateProductDTO.categoryId() != null) {
            product.setCategoryId(updateProductDTO.categoryId());
        }
    }

    public static void updateEntity(Product product, OrderedProductDTO orderedProductDTO) {
        if (product == null || orderedProductDTO == null) return;

        if (orderedProductDTO.productId() != null) {
            product.setId(orderedProductDTO.productId());
        }
        if (orderedProductDTO.quantity() != null) {
            product.setQuantity(orderedProductDTO.quantity());
        }
    }

    public static void updateView(ProductView product, ProductUpdatedEvent updateProductDTO) {
        if (product == null || updateProductDTO == null) return;

        if (updateProductDTO.getProductId() != null) {
            product.setProductId(updateProductDTO.getProductId());
        }
        if (updateProductDTO.getName() != null) {
            product.setName(updateProductDTO.getName());
        }
        if (updateProductDTO.getPrice() != null) {
            product.setPrice(updateProductDTO.getPrice());
        }
        if (updateProductDTO.getQuantity() != null) {
            product.setQuantity(updateProductDTO.getQuantity());
        }
        if (updateProductDTO.getDescription() != null) {
            product.setDescription(updateProductDTO.getDescription());
        }
        if (updateProductDTO.getImage() != null) {
            product.setImage(updateProductDTO.getImage());
        }
        if (updateProductDTO.getCategoryId() != null) {
            product.setCategoryId(updateProductDTO.getCategoryId());
        }
    }
}
