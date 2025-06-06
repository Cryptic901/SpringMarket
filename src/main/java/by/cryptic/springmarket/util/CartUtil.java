package by.cryptic.springmarket.util;

import by.cryptic.springmarket.model.write.AppUser;
import by.cryptic.springmarket.model.write.Cart;
import by.cryptic.springmarket.model.write.CartProduct;
import by.cryptic.springmarket.repository.write.CartRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

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

    public void clearCart(AppUser user) {
        Cart cart = cartRepository.findByUserIdWithItems(user.getId())
                .orElseThrow(() -> new EntityNotFoundException
                        ("No cart found with userId %s".formatted(user.getId())));
        clearCart(cart);
    }
}
