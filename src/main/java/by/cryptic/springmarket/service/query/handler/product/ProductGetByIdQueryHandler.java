package by.cryptic.springmarket.service.query.handler.product;

import by.cryptic.springmarket.dto.FullProductDto;
import by.cryptic.springmarket.mapper.FullProductMapper;
import by.cryptic.springmarket.repository.read.ProductViewRepository;
import by.cryptic.springmarket.service.query.handler.QueryHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"products"})
public class ProductGetByIdQueryHandler implements QueryHandler<UUID, FullProductDto> {

    private final FullProductMapper fullProductMapper;
    private final ProductViewRepository productViewRepository;

    @Cacheable(key = "'product:' + #id")
    public FullProductDto handle(UUID id) {
        return fullProductMapper.toDto(productViewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id : %s".formatted(id))));
    }
}
