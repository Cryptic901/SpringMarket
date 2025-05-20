package by.cryptic.springmarket.service;

import by.cryptic.springmarket.dto.AddCartProductDTO;
import by.cryptic.springmarket.model.AppUser;
import by.cryptic.springmarket.model.Cart;
import by.cryptic.springmarket.model.CartProduct;
import by.cryptic.springmarket.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"carts"})
public class CartService {

    private final CartRepository cartRepository;

    @CachePut(key = "#user.id")
    public void addProductToCart(AddCartProductDTO productToAdd, AppUser user) {
        boolean found = false;
        Cart cart = user.getCart();
        List<CartProduct> products = cart.getItems();
        for (CartProduct product : products) {
            if (product.getId().equals(productToAdd.id())) {
                product.setQuantity(product.getQuantity() + productToAdd.quantity());
                found = true;
                break;
            }
        }
        if (!found) {
            products.add(CartProduct.builder()
                    .id(productToAdd.id())
                    .quantity(productToAdd.quantity())
                    .price(productToAdd.price())
                    .build());
        }
        cartRepository.save(cart);
    }
    @Transactional
    @CacheEvict(key = "#user.id")
    public void deleteAllProductsFromCart(AppUser user) {
        user.getCart().getItems().clear();
    }

    @CacheEvict(key = "#user.id")
    public void deleteProductFromCart(UUID id, AppUser user) {
        List<CartProduct> cart = user.getCart().getItems();
        for (CartProduct cartProduct : cart) {
            if (cartProduct.getProduct().getId().equals(id)) {
                cartProduct.setQuantity(cartProduct.getQuantity() - 1);
            }
        }
    }

    @Cacheable(key = "#user.id")
    public BigDecimal getTotalPrice(AppUser user) {
        List<CartProduct> cart = user.getCart().getItems();
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartProduct cartProduct : cart) {
            totalPrice = totalPrice.add(cartProduct.getPrice()
                    .multiply(BigDecimal.valueOf(cartProduct.getQuantity())));
        }
        return totalPrice;
    }
}
