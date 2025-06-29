package by.cryptic.productservice.controller.command;
import by.cryptic.productservice.service.command.product.ProductCreateCommand;
import by.cryptic.productservice.service.command.product.ProductDeleteCommand;
import by.cryptic.productservice.service.command.product.ProductUpdateCommand;
import by.cryptic.productservice.service.command.product.handler.ProductCreateCommandHandler;
import by.cryptic.productservice.service.command.product.handler.ProductDeleteCommandHandler;
import by.cryptic.productservice.service.command.product.handler.ProductUpdateCommandHandler;
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

    @PatchMapping
    public ResponseEntity<Void> updateProduct(@RequestBody ProductUpdateCommand product) {
        productUpdateCommandHandler.handle(product);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productDeleteCommandHandler.handle(new ProductDeleteCommand(id));
        return ResponseEntity.noContent().build();
    }
}