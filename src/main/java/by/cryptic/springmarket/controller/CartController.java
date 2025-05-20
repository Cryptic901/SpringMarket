package by.cryptic.springmarket.controller;

import by.cryptic.springmarket.dto.AddCartProductDTO;
import by.cryptic.springmarket.model.AppUser;
import by.cryptic.springmarket.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<Void> addItemToCart(
            @RequestBody AddCartProductDTO addCartProductDTO,
            @AuthenticationPrincipal AppUser user) {
        cartService.addProductToCart(addCartProductDTO, user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> removeAllItemsFromCart(
            @AuthenticationPrincipal AppUser user) {
        cartService.deleteAllProductsFromCart(user);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeItemFromCart(
            @PathVariable UUID id,
            @AuthenticationPrincipal AppUser user) {
        cartService.deleteProductFromCart(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<BigDecimal> getTotalPrice(
            @AuthenticationPrincipal AppUser user) {
        return ResponseEntity.ok(cartService.getTotalPrice(user));
    }
}
