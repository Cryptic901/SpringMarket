package by.cryptic.orderservice.client;

import by.cryptic.orderservice.config.FeignClientConfig;
import by.cryptic.utils.DTO.CartProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "cart-service", path = "/api/v1/carts", configuration = FeignClientConfig.class)
public interface CartServiceClient {

    @DeleteMapping("/internal/clear/{userId}")
    ResponseEntity<Void> removeAllItemsFromCartByUserId(@PathVariable UUID userId);

    @GetMapping("/internal/user/{userId}")
    ResponseEntity<List<CartProductDTO>> getCartProductsByUserId(@PathVariable UUID userId);
}
