package by.cryptic.cartservice.util;

import by.cryptic.cartservice.model.write.Cart;
import by.cryptic.cartservice.model.write.CartProduct;
import by.cryptic.cartservice.repository.write.CartRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
public class CartUtil {

    private final CartRepository cartRepository;

    public CartUtil(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public BigDecimal getTotalPrice(List<CartProduct> cart) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartProduct cartProduct : cart) {
            totalPrice = totalPrice.add(cartProduct.getPricePerUnit()
                    .multiply(BigDecimal.valueOf(cartProduct.getQuantity())));
        }
        return totalPrice;
    }

    public void clearCart(Cart cart) {
        cart.getItems().clear();
        cart.setTotal(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    public void clearCart(UUID userId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new EntityNotFoundException
                        ("No cart found with userId %s".formatted(userId)));
        clearCart(cart);
    }
}
