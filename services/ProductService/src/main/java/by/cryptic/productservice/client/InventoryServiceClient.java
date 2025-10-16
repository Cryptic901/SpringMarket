package by.cryptic.productservice.client;

import by.cryptic.productservice.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "InventoryService", path = "/api/v1/warehouses", configuration = FeignClientConfig.class)
public interface InventoryServiceClient {

    @GetMapping("/check-capacity")
    ResponseEntity<Boolean> checkCapacity(@RequestParam Integer quantity);
}
