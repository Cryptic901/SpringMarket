package by.cryptic.productservice.service.query.handler;

import by.cryptic.productservice.mapper.ProductMapper;
import by.cryptic.productservice.model.read.ProductView;
import by.cryptic.productservice.model.write.Product;
import by.cryptic.productservice.repository.read.ProductViewRepository;
import by.cryptic.utils.DTO.ProductDTO;
import by.cryptic.utils.QueryHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductGetByIdQueryHandler implements QueryHandler<UUID, ProductDTO> {

    private final ProductViewRepository productRepository;
    private final CacheManager cacheManager;

    @Override
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
                return ProductMapper.toDto(cachedProduct);
            }
        }

        ProductView dbProduct = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with id %s was not found"
                        .formatted(id)));
        if (cache != null) {
            cache.put(cacheKey, dbProduct);
        }
        return ProductMapper.toDto(dbProduct);
    }
}
