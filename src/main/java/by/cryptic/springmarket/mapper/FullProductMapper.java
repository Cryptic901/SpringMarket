package by.cryptic.springmarket.mapper;

import by.cryptic.springmarket.dto.FullProductDto;
import by.cryptic.springmarket.model.read.ProductView;
import by.cryptic.springmarket.model.write.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FullProductMapper {

    Product toEntity(FullProductDto fullProductDto);

    FullProductDto toDto(Product product);
    FullProductDto toDto(ProductView productView);

}
