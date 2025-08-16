package by.cryptic.categoryservice.service.query.handler;

import by.cryptic.categoryservice.dto.CategoryDTO;
import by.cryptic.categoryservice.mapper.CategoryMapper;
import by.cryptic.categoryservice.repository.read.CategoryViewRepository;
import by.cryptic.utils.QueryHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@CacheConfig(cacheNames = {"categories"})
public class CategoryGetByIdQueryHandler implements QueryHandler<UUID, CategoryDTO> {

    private final CategoryViewRepository categoryViewRepository;

    @Override
    @Cacheable(key = "'category:' + #result.name()")
    public CategoryDTO handle(UUID uuid) {
        return CategoryMapper.toDto(categoryViewRepository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: %s".formatted(uuid))));
    }
}
