package by.cryptic.orderservice.client;

import by.cryptic.orderservice.config.FeignClientConfig;
import by.cryptic.utils.DTO.CartProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "CartService", path = "/api/v1/carts", configuration = FeignClientConfig.class)
public interface CartServiceClient {

    @DeleteMapping("/clear")
    ResponseEntity<Void> removeAllItemsFromCart(UUID userId);

    @GetMapping
    ResponseEntity<List<CartProductDTO>> getCartProducts();
}
