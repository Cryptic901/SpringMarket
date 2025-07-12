package by.cryptic.cartservice.mapper;

import by.cryptic.cartservice.dto.CartDTO;
import by.cryptic.cartservice.model.read.CartProductView;
import by.cryptic.cartservice.model.read.CartView;
import by.cryptic.cartservice.model.write.Cart;
import by.cryptic.cartservice.model.write.CartProduct;
import org.springframework.stereotype.Component;

@Component
public class CartMapper {

    public static CartDTO toDto(Cart cart) {
        if (cart == null) {
            return null;
        }
        return CartDTO.builder()
                .userId(cart.getUserId())
                .id(cart.getId())
                .total(cart.getTotal())
                .build();
    }

    public static CartView toView(Cart cart) {
        if (cart == null) {
            return null;
        }
        return CartView.builder()
                .cartId(cart.getId())
                .userId(cart.getUserId())
                .total(cart.getTotal())
                .products(cart.getItems().stream().map(CartMapper::toProductView).toList())
                .build();
    }

    public static Cart toEntity(CartDTO cartDTO) {
        if (cartDTO == null) {
            return null;
        }
        return Cart.builder()
                .userId(cartDTO.getUserId())
                .total(cartDTO.getTotal())
                .id(cartDTO.getId())
                .build();
    }

    public static Cart toEntity(CartView cartView) {
        if (cartView == null) {
            return null;
        }
        return Cart.builder()
                .userId(cartView.getUserId())
                .total(cartView.getTotal())
                .id(cartView.getCartId())
                .build();
    }

    public static CartProductView toProductView(CartProduct cartProduct) {
        if (cartProduct == null) {
            return null;
        }
        return CartProductView.builder()
                .productId(cartProduct.getProductId())
                .quantity(cartProduct.getQuantity())
                .price(cartProduct.getPricePerUnit())
                .build();
    }
}
