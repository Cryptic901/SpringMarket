package by.cryptic.productservice.client;

import by.cryptic.productservice.config.FeignClientConfig;
import by.cryptic.utils.DTO.CategoryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "CategoryService", path = "/api/v1/categories", configuration = FeignClientConfig.class)
public interface CategoryServiceClient {

    @GetMapping("/{id}")
    ResponseEntity<CategoryDTO> getCategoryById(@PathVariable UUID id);
}
