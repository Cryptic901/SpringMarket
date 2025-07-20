package by.cryptic.cartservice.controller.command;

import by.cryptic.cartservice.service.command.CartAddCommand;
import by.cryptic.cartservice.service.command.CartClearCommand;
import by.cryptic.cartservice.service.command.CartDeleteProductCommand;
import by.cryptic.cartservice.service.command.handler.CartAddCommandHandler;
import by.cryptic.cartservice.service.command.handler.CartClearCommandHandler;
import by.cryptic.cartservice.service.command.handler.CartDeleteProductCommandHandler;
import by.cryptic.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartCommandController {

    private final CartAddCommandHandler cartAddCommandHandler;
    private final CartDeleteProductCommandHandler cartDeleteProductCommandHandler;
    private final CartClearCommandHandler cartClearCommandHandler;

    @PostMapping
    public ResponseEntity<Void> addItemToCart(
            @RequestParam UUID productId, @AuthenticationPrincipal Jwt jwt) {
        cartAddCommandHandler.handle(new CartAddCommand(productId, JwtUtil.extractUserId(jwt)));
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> removeAllItemsFromCart(@AuthenticationPrincipal Jwt jwt) {
        cartClearCommandHandler.handle(new CartClearCommand(JwtUtil.extractUserId(jwt)));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> removeItemFromCart(
            @RequestParam UUID productId, @AuthenticationPrincipal Jwt jwt) {
        cartDeleteProductCommandHandler.handle(new CartDeleteProductCommand(productId,
                JwtUtil.extractUserId(jwt)));
        return ResponseEntity.noContent().build();
    }
}
