package by.cryptic.springmarket.controller.query;

import by.cryptic.springmarket.dto.ProductDTO;
import by.cryptic.springmarket.service.query.SortParamsDTO;
import by.cryptic.springmarket.service.query.handler.product.ProductGetAllQueryHandler;
import by.cryptic.springmarket.service.query.handler.product.ProductGetByIdQueryHandler;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductQueryController {

    private final ProductGetAllQueryHandler productGetAllQueryHandler;
    private final ProductGetByIdQueryHandler productGetByIdQueryHandler;

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
        return ResponseEntity.ok(productGetAllQueryHandler.handle
                (new SortParamsDTO(name, createdBy, category, min,
                        max, page, size, sortBy, order)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(productGetByIdQueryHandler.handle(id));
    }
}
