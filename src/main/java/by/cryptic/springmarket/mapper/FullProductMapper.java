package by.cryptic.springmarket.mapper;

import by.cryptic.springmarket.dto.FullProductDto;
import by.cryptic.springmarket.dto.ProductDTO;
import by.cryptic.springmarket.model.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FullProductMapper {

    Product toEntity(FullProductDto fullProductDto);

    FullProductDto toDto(Product product);

}
