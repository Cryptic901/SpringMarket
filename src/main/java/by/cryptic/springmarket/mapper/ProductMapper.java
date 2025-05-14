package by.cryptic.springmarket.mapper;

import by.cryptic.springmarket.dto.FullProductDto;
import by.cryptic.springmarket.dto.ProductDTO;
import by.cryptic.springmarket.dto.UpdateProductDTO;
import by.cryptic.springmarket.model.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toEntity(ProductDTO productDTO);

    ProductDTO toDto(Product product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Product product, UpdateProductDTO updateProductDTO);
}
