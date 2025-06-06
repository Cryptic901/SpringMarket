package by.cryptic.springmarket.controller.command;

import by.cryptic.springmarket.service.command.CartAddCommand;
import by.cryptic.springmarket.service.command.CartClearCommand;
import by.cryptic.springmarket.service.command.CartDeleteProductCommand;
import by.cryptic.springmarket.service.command.handler.cart.CartAddCommandHandler;
import by.cryptic.springmarket.service.command.handler.cart.CartClearCommandHandler;
import by.cryptic.springmarket.service.command.handler.cart.CartDeleteProductCommandHandler;
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
            @RequestParam UUID productId) {
        cartAddCommandHandler.handle(new CartAddCommand(productId));
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> removeAllItemsFromCart() {
        cartClearCommandHandler.handle(new CartClearCommand());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> removeItemFromCart(
            @RequestParam UUID productId) {
        cartDeleteProductCommandHandler.handle(new CartDeleteProductCommand(productId));
        return ResponseEntity.noContent().build();
    }
}
