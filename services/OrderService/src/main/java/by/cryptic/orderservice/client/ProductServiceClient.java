package by.cryptic.orderservice.client;

import by.cryptic.utils.DTO.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "ProductService", path = "/api/v1/products")
public interface ProductServiceClient {

    @GetMapping("/{id}")
    ResponseEntity<ProductDTO> getProductById(@PathVariable UUID id);
}
