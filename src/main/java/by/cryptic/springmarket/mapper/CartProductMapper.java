package by.cryptic.springmarket.mapper;

import by.cryptic.springmarket.dto.AddCartProductDTO;
import by.cryptic.springmarket.model.CartProduct;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartProductMapper {

    AddCartProductDTO toDto(CartProduct cartProduct);

    CartProduct toEntity(AddCartProductDTO cartProductDTO);

}
