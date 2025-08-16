package by.cryptic.cartservice.mapper;

import by.cryptic.cartservice.model.read.CartProductView;
import by.cryptic.utils.DTO.CartProductDTO;
import org.springframework.stereotype.Component;

@Component
public class CartMapper {

    public static CartProductDTO toDto(CartProductView cartProductView) {
        if (cartProductView == null) {
            return null;
        }
        return CartProductDTO.builder()
                .pricePerUnit(cartProductView.getPrice())
                .productId(cartProductView.getProductId())
                .quantity(cartProductView.getQuantity())
                .build();
    }
}
