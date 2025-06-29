package by.cryptic.cartservice.mapper;

import by.cryptic.cartservice.dto.CartDTO;
import by.cryptic.cartservice.model.read.CartView;
import by.cryptic.cartservice.model.write.Cart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartMapper {

    CartDTO toDto(Cart cart);

    CartView toView(Cart cart);
    Cart toEntity(CartDTO cartDTO);
    Cart toEntity(CartView cartView);
}
