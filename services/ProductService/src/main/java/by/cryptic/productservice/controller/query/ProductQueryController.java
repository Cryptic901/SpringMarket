package by.cryptic.productservice.controller.query;

import by.cryptic.productservice.service.query.SortParamsQuery;
import by.cryptic.productservice.service.query.handler.ProductGetAllQueryHandler;
import by.cryptic.productservice.service.query.handler.ProductGetByIdQueryHandler;
import by.cryptic.utils.DTO.ProductDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Get all products", description = "return all products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products found"),
            @ApiResponse(responseCode = "404", description = "Products not found"),
    })
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
                (new SortParamsQuery(name, createdBy, category, min,
                        max, page, size, sortBy, order)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by id", description = "return product by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
    })
    public ResponseEntity<ProductDTO> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(productGetByIdQueryHandler.handle(id));
    }
}