package by.cryptic.springmarket.service;

import by.cryptic.springmarket.dto.CartProductDTO;
import by.cryptic.springmarket.dto.CartResponseDTO;
import by.cryptic.springmarket.model.AppUser;
import by.cryptic.springmarket.model.Cart;
import by.cryptic.springmarket.model.CartProduct;
import by.cryptic.springmarket.model.Product;
import by.cryptic.springmarket.repository.CartProductRepository;
import by.cryptic.springmarket.repository.CartRepository;
import by.cryptic.springmarket.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"carts"})
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartProductRepository cartProductRepository;

    @Transactional(readOnly = true)
    public List<CartProductDTO> getAllCartProducts(AppUser user) {
        return cartRepository.findByUserIdWithItems(user.getId())
                .map(Cart::getItems)
                .orElse(Collections.emptyList())
                .stream().map(pr ->
                        new CartProductDTO(pr.getProduct().getId(), pr.getQuantity(), pr.getPricePerUnit()))
                .toList();
    }

    @CachePut(key = "'cart:' + #user.id")
    @Transactional
    public CartResponseDTO addProductToCart(UUID productId, AppUser user) {
        Cart cart = cartRepository.findByUserIdWithItems(user.getId())
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .total(BigDecimal.ZERO)
                            .items(new ArrayList<>())
                            .build();
                    return cartRepository.save(newCart);
                });
        log.info("Cart after mapping to entity: {}", cart);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("You're trying to add product that doesn't exists"));
        List<CartProduct> products = cart.getItems();
        CartProduct cartProduct = products
                .stream()
                .filter(cp -> cp.getProduct().getId()
                        .equals(productId))
                .findFirst()
                .orElse(null);
        if (cartProduct != null) {
            cartProduct.setQuantity(cartProduct.getQuantity() + 1);
        } else {
            products.add(CartProduct.builder()
                    .pricePerUnit(product.getPrice())
                    .product(product)
                    .quantity(1)
                    .cart(cart)
                    .build());
        }

        cart.setTotal(getTotalPrice(products));
        cart.setUser(user);

        cartRepository.save(cart);
//        cartProductRepository.saveAll(products);

        return new CartResponseDTO(cart.getTotal());
    }

    @Transactional
    @CacheEvict(key = "'cart:' + #user.id")
    public void deleteAllProductsFromCart(AppUser user) {
        cartRepository.findByUserIdWithItems(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("You don't have any products in your cart"))
                .getItems().clear();
        user.getCart().setTotal(BigDecimal.ZERO);
    }

    @CacheEvict(key = "'cart:' + #user.id")
    @Transactional
    public void deleteProductFromCart(UUID id, AppUser user) {
        List<CartProduct> cart = cartRepository.findByUserIdWithItems(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("You don't have any products in your cart"))
                .getItems();
        Iterator<CartProduct> iterator = cart.iterator();
        while (iterator.hasNext()) {
            CartProduct cartProduct = iterator.next();
            if (cartProduct.getProduct().getId().equals(id)) {
                if (cartProduct.getQuantity() > 1) {
                    cartProduct.setQuantity(cartProduct.getQuantity() - 1);
                    cartProductRepository.save(cartProduct);
                } else {
                    iterator.remove();
                    cartProductRepository.delete(cartProduct);
                }
            }
        }
    }

    private BigDecimal getTotalPrice(List<CartProduct> cart) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartProduct cartProduct : cart) {
            totalPrice = totalPrice.add(cartProduct.getPricePerUnit()
                    .multiply(BigDecimal.valueOf(cartProduct.getQuantity())));
        }
        return totalPrice;
    }
}
