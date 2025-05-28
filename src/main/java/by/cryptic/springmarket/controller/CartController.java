package by.cryptic.springmarket.controller;

import by.cryptic.springmarket.dto.CartProductDTO;
import by.cryptic.springmarket.dto.CartResponseDTO;
import by.cryptic.springmarket.model.AppUser;
import by.cryptic.springmarket.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<List<CartProductDTO>> getCartProducts(@AuthenticationPrincipal AppUser user) {
        return ResponseEntity.ok(cartService.getAllCartProducts(user));
    }

    @PostMapping
    public ResponseEntity<CartResponseDTO> addItemToCart(
            @RequestParam UUID productId,
            @AuthenticationPrincipal AppUser user) {
        return ResponseEntity.ok(cartService.addProductToCart(productId, user));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> removeAllItemsFromCart(
            @AuthenticationPrincipal AppUser user) {
        cartService.deleteAllProductsFromCart(user);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> removeItemFromCart(
            @RequestParam UUID productId,
            @AuthenticationPrincipal AppUser user) {
        cartService.deleteProductFromCart(productId, user);
        return ResponseEntity.noContent().build();
    }
}
