package by.cryptic.cartservice.controller.command;

import by.cryptic.cartservice.service.command.CartAddCommand;
import by.cryptic.cartservice.service.command.CartClearCommand;
import by.cryptic.cartservice.service.command.CartDeleteProductCommand;
import by.cryptic.cartservice.service.command.handler.CartAddCommandHandler;
import by.cryptic.cartservice.service.command.handler.CartClearCommandHandler;
import by.cryptic.cartservice.service.command.handler.CartDeleteProductCommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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
            @RequestParam UUID productId,
            @RequestHeader("X-User-Id") UUID userId) {
        cartAddCommandHandler.handle(new CartAddCommand(productId, userId));
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> removeAllItemsFromCart(
            @RequestHeader("X-User-Id") UUID userId
    ) {
        cartClearCommandHandler.handle(new CartClearCommand(userId));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> removeItemFromCart(
            @RequestParam UUID productId,
            @RequestHeader("X-User-Id") UUID userId) {
        cartDeleteProductCommandHandler.handle(new CartDeleteProductCommand(productId, userId));
        return ResponseEntity.noContent().build();
    }
}
