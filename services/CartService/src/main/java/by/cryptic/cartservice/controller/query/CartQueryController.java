package by.cryptic.cartservice.controller.query;

import by.cryptic.cartservice.service.query.CartGetAllQuery;
import by.cryptic.cartservice.service.query.handler.CartGetAllQueryHandler;
import by.cryptic.security.JwtUtil;
import by.cryptic.utils.DTO.CartProductDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartQueryController {

    private final CartGetAllQueryHandler cartGetAllQueryHandler;

    @GetMapping
    public ResponseEntity<List<CartProductDTO>> getCartProducts(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(cartGetAllQueryHandler.handle(new CartGetAllQuery(JwtUtil.extractUserId(jwt))));
    }
}
