package by.cryptic.springmarket.mapper;

import by.cryptic.springmarket.dto.ProductDTO;
import by.cryptic.springmarket.event.product.ProductUpdatedEvent;
import by.cryptic.springmarket.model.read.ProductView;
import by.cryptic.springmarket.model.write.Product;
import by.cryptic.springmarket.service.command.ProductUpdateCommand;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toEntity(ProductDTO productDTO);

    ProductDTO toDto(Product product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Product product, ProductUpdateCommand updateProductDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateView(@MappingTarget ProductView product, ProductUpdatedEvent updateProductDTO);
}
