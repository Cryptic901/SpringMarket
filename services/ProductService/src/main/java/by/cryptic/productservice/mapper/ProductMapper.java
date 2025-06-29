package by.cryptic.productservice.mapper;

import by.cryptic.productservice.dto.ProductDTO;
import by.cryptic.utils.event.product.ProductUpdatedEvent;
import by.cryptic.productservice.model.read.ProductView;
import by.cryptic.productservice.model.write.Product;
import by.cryptic.productservice.service.command.product.ProductUpdateCommand;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProductMapper {


    ProductDTO toDto(ProductView product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Product product, ProductUpdateCommand updateProductDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateView(@MappingTarget ProductView product, ProductUpdatedEvent updateProductDTO);
}
