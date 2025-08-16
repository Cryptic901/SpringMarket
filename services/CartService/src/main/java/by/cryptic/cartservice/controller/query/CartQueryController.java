package by.cryptic.cartservice.controller.query;

import by.cryptic.cartservice.service.query.CartGetAllQuery;
import by.cryptic.cartservice.service.query.CartGetByIdQuery;
import by.cryptic.cartservice.service.query.handler.CartGetAllQueryHandler;
import by.cryptic.cartservice.service.query.handler.CartGetByIdQueryHandler;
import by.cryptic.security.JwtUtil;
import by.cryptic.utils.DTO.CartProductDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartQueryController {

    private final CartGetAllQueryHandler cartGetAllQueryHandler;
    private final CartGetByIdQueryHandler cartGetByIdQueryHandler;

    @GetMapping
    public ResponseEntity<List<CartProductDTO>> getCartProducts(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(cartGetAllQueryHandler
                .handle(new CartGetAllQuery(JwtUtil.extractUserId(jwt))));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<CartProductDTO> getCartProductById(@AuthenticationPrincipal Jwt jwt,
                                                             @PathVariable UUID productId) {
        return ResponseEntity.ok(cartGetByIdQueryHandler
                .handle(new CartGetByIdQuery(JwtUtil.extractUserId(jwt), productId)));
    }
}
