package by.cryptic.springmarket.controller.command;

import by.cryptic.springmarket.service.command.ProductCreateCommand;
import by.cryptic.springmarket.service.command.ProductDeleteCommand;
import by.cryptic.springmarket.service.command.ProductUpdateCommand;
import by.cryptic.springmarket.service.command.handler.product.ProductCreateCommandHandler;
import by.cryptic.springmarket.service.command.handler.product.ProductDeleteCommandHandler;
import by.cryptic.springmarket.service.command.handler.product.ProductUpdateCommandHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Void> createProduct(@RequestBody @Valid ProductCreateCommand product) {
        productCreateCommandHandler.handle(product);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateProduct(@PathVariable UUID id, @RequestBody ProductUpdateCommand product) {
        productUpdateCommandHandler.handle(product);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productDeleteCommandHandler.handle(new ProductDeleteCommand(id));
        return ResponseEntity.noContent().build();
    }
}
