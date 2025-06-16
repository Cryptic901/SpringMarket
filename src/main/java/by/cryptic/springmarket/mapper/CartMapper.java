package by.cryptic.springmarket.mapper;

import by.cryptic.springmarket.dto.CartDTO;
import by.cryptic.springmarket.model.read.CartView;
import by.cryptic.springmarket.model.write.Cart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartMapper {

    CartDTO toDto(Cart cart);

    CartView toView(Cart cart);
    Cart toEntity(CartDTO cartDTO);
    Cart toEntity(CartView cartView);
}
