package by.cryptic.productservice.service.query.product.handler;

import by.cryptic.productservice.mapper.ProductMapper;
import by.cryptic.productservice.model.write.Product;
import by.cryptic.productservice.repository.write.ProductRepository;
import by.cryptic.utils.DTO.ProductDTO;
import by.cryptic.utils.QueryHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@CacheConfig(cacheNames = {"products"})
public class ProductGetByIdQueryHandler implements QueryHandler<UUID, ProductDTO> {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CacheManager cacheManager;

    public ProductDTO handle(UUID id) {
        return findInCacheOrDB(id);
    }

    public ProductDTO findInCacheOrDB(UUID id) {
        String cacheKey = "product:" + id;
        Cache cache = cacheManager.getCache("products");
        if (cache != null) {
            Product cachedProduct = cache.get(cacheKey, Product.class);
            if (cachedProduct != null) {
                log.debug("Product was found in cache {}", cachedProduct);
                return productMapper.toDto(cachedProduct);
            }
        }

        Product dbProduct = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with id %s was not found"
                        .formatted(id)));
        if (cache != null) {
            cache.put(cacheKey, dbProduct);
        }
        return productMapper.toDto(dbProduct);
    }
}
