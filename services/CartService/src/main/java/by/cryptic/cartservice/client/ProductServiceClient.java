package by.cryptic.cartservice.client;

import by.cryptic.cartservice.config.FeignClientConfig;
import by.cryptic.utils.DTO.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "ProductService", path = "api/v1/products", configuration = FeignClientConfig.class)
public interface ProductServiceClient {

    @GetMapping("/{id}")
    ResponseEntity<ProductDTO> getProductById(@PathVariable UUID id);
}
