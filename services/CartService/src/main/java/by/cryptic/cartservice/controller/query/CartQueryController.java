package by.cryptic.cartservice.controller.query;

import by.cryptic.cartservice.service.query.CartGetAllQuery;
import by.cryptic.cartservice.service.query.CartGetByIdQuery;
import by.cryptic.cartservice.service.query.handler.CartGetAllQueryHandler;
import by.cryptic.cartservice.service.query.handler.CartGetByIdQueryHandler;
import by.cryptic.security.JwtUtil;
import by.cryptic.utils.DTO.CartProductDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartQueryController {

    private final CartGetAllQueryHandler cartGetAllQueryHandler;
    private final CartGetByIdQueryHandler cartGetByIdQueryHandler;

    @GetMapping
    @Operation(summary = "Get all products in cart", description = "return all products in cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart products found"),
            @ApiResponse(responseCode = "404", description = "Cart products not found"),
    })
    public ResponseEntity<List<CartProductDTO>> getCartProducts(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(cartGetAllQueryHandler
                .handle(new CartGetAllQuery(JwtUtil.extractUserId(jwt))));
    }

    @GetMapping("/internal/user/{userId}")
    @Operation(summary = "Get cart products by user id", description = "return all products by user id. " +
            "This endpoint is needed for internal communication for other clients.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart products found"),
            @ApiResponse(responseCode = "404", description = "Cart products not found"),
    })
    public ResponseEntity<List<CartProductDTO>> getCartProductsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(cartGetAllQueryHandler
                .handle(new CartGetAllQuery(userId)));
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get cart product by id", description = "return cart product by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart product found"),
            @ApiResponse(responseCode = "404", description = "Cart or cart product not found"),
    })
    public ResponseEntity<CartProductDTO> getCartProductById(@AuthenticationPrincipal Jwt jwt,
                                                             @PathVariable UUID productId) {
        return ResponseEntity.ok(cartGetByIdQueryHandler
                .handle(new CartGetByIdQuery(JwtUtil.extractUserId(jwt), productId)));
    }
}
