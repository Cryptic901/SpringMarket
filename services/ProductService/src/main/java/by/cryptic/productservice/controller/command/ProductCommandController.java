package by.cryptic.productservice.controller.command;
import by.cryptic.productservice.dto.ProductUpdateDTO;
import by.cryptic.productservice.service.command.product.ProductCreateCommand;
import by.cryptic.productservice.service.command.product.ProductDeleteCommand;
import by.cryptic.productservice.service.command.product.ProductUpdateCommand;
import by.cryptic.productservice.service.command.product.handler.ProductCreateCommandHandler;
import by.cryptic.productservice.service.command.product.handler.ProductDeleteCommandHandler;
import by.cryptic.productservice.service.command.product.handler.ProductUpdateCommandHandler;
import by.cryptic.security.JwtUtil;
import by.cryptic.utils.DTO.ProductDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductCommandController {

    private final ProductCreateCommandHandler productCreateCommandHandler;
    private final ProductUpdateCommandHandler productUpdateCommandHandler;
    private final ProductDeleteCommandHandler productDeleteCommandHandler;

    @PostMapping
    public ResponseEntity<Void> createProduct(@RequestBody @Valid ProductDTO product,
                                              @AuthenticationPrincipal Jwt jwt) {
        productCreateCommandHandler.handle(new ProductCreateCommand(
                product.name(),
                JwtUtil.extractUserId(jwt),
                product.price(),
                product.quantity(),
                product.description(),
                product.image(),
                product.categoryId()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<Void> updateProduct(@RequestBody ProductUpdateDTO product,
                                              @PathVariable UUID productId,
                                              @AuthenticationPrincipal Jwt jwt) {
        productUpdateCommandHandler.handle(new ProductUpdateCommand(
                productId,
                product.name(),
                product.price(),
                product.quantity(),
                product.description(),
                product.image(),
                JwtUtil.extractUserId(jwt),
                product.categoryId()
        ));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id,
                                              @AuthenticationPrincipal Jwt jwt) {
        productDeleteCommandHandler.handle(new ProductDeleteCommand(id, JwtUtil.extractUserId(jwt)));
        return ResponseEntity.noContent().build();
    }
}