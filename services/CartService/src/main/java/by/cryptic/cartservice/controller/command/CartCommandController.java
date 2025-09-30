package by.cryptic.cartservice.controller.command;

import by.cryptic.cartservice.service.command.CartAddCommand;
import by.cryptic.cartservice.service.command.CartClearCommand;
import by.cryptic.cartservice.service.command.CartDeleteProductCommand;
import by.cryptic.cartservice.service.command.handler.CartAddCommandHandler;
import by.cryptic.cartservice.service.command.handler.CartClearCommandHandler;
import by.cryptic.cartservice.service.command.handler.CartDeleteProductCommandHandler;
import by.cryptic.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @PostMapping("/{productId}")
    @Operation(summary = "Add item to cart", description = "adding item to cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item added"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "409", description = "Not enough products"),
            @ApiResponse(responseCode = "503", description = "The adding failed because the server is down")
    })
    public ResponseEntity<Void> addItemToCart(
            @PathVariable UUID productId, @AuthenticationPrincipal Jwt jwt) {
        cartAddCommandHandler.handle(new CartAddCommand(productId, JwtUtil.extractUserId(jwt)));
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).build();
    }

    @DeleteMapping("/clear")
    @Operation(summary = "Clear cart", description = "clearing cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cart cleared"),
            @ApiResponse(responseCode = "404", description = "Cart not found"),
    })
    public ResponseEntity<Void> removeAllItemsFromCart(@AuthenticationPrincipal Jwt jwt) {
        cartClearCommandHandler.handle(new CartClearCommand(JwtUtil.extractUserId(jwt)));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("internal/clear/{userId}")
    @Operation(summary = "Clear cart", description = "clearing cart" +
            "This endpoint is needed for internal communication for other clients.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cart cleared"),
            @ApiResponse(responseCode = "404", description = "Cart not found"),
    })
    public ResponseEntity<Void> removeAllItemsFromCartByUserId(@PathVariable UUID userId) {
        cartClearCommandHandler.handle(new CartClearCommand(userId));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Remove item from cart by id", description = "removing item from cart by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Cart is empty"),
            @ApiResponse(responseCode = "404", description = "Cart not found"),
            @ApiResponse(responseCode = "503", description = "The adding failed because the server is down")
    })
    public ResponseEntity<Void> removeItemFromCart(
            @PathVariable UUID productId, @AuthenticationPrincipal Jwt jwt) {
        cartDeleteProductCommandHandler.handle(new CartDeleteProductCommand(productId,
                JwtUtil.extractUserId(jwt)));
        return ResponseEntity.noContent().build();
    }
}
