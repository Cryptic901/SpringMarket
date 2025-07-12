package by.cryptic.productservice.service.query.product.handler;

import by.cryptic.utils.DTO.ProductDTO;
import by.cryptic.productservice.mapper.ProductMapper;
import by.cryptic.productservice.repository.read.ProductViewRepository;
import by.cryptic.utils.QueryHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"products"})
public class ProductGetByIdQueryHandler implements QueryHandler<UUID, ProductDTO> {

    private final ProductViewRepository productViewRepository;
    private final ProductMapper productMapper;

    @Cacheable(key = "'product:' + #id")
    public ProductDTO handle(UUID id) {
        return productMapper.toDto(productViewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id : %s".formatted(id))));
    }
}
