package by.cryptic.springmarket.controller.query;

import by.cryptic.springmarket.service.query.CartGetAllQuery;
import by.cryptic.springmarket.service.query.CartProductDTO;
import by.cryptic.springmarket.service.query.handler.cart.CartGetAllQueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<CartProductDTO>> getCartProducts() {
        return ResponseEntity.ok(cartGetAllQueryHandler.handle(new CartGetAllQuery()));
    }
}
