package by.cryptic.springmarket.controller;

import by.cryptic.springmarket.dto.CreateProductDTO;
import by.cryptic.springmarket.dto.FullProductDto;
import by.cryptic.springmarket.dto.ProductDTO;
import by.cryptic.springmarket.dto.UpdateProductDTO;
import by.cryptic.springmarket.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String createdBy,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @Min(1) BigDecimal min,
            @RequestParam(required = false) BigDecimal max,
            @RequestParam(required = false, defaultValue = "1") @Min(1) Integer page,
            @RequestParam(required = false, defaultValue = "20") @Min(5) @Max(200) Integer size,
            @RequestParam(required = false, defaultValue = "price") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String order
    ) {
        return ResponseEntity.ok(productService.getAllProducts(name, createdBy, category, min,
                max, page, size, sortBy, order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FullProductDto> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<FullProductDto> createProduct(@RequestBody @Valid CreateProductDTO product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(product));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FullProductDto> updateProduct(@PathVariable UUID id, @RequestBody UpdateProductDTO product) {
        return ResponseEntity.ok(productService.update(id, product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
