package by.cryptic.categoryservice.service.query.handler;

import by.cryptic.utils.DTO.CategoryDTO;
import by.cryptic.categoryservice.mapper.CategoryMapper;
import by.cryptic.categoryservice.model.read.CategoryView;
import by.cryptic.categoryservice.model.write.Category;
import by.cryptic.categoryservice.repository.read.CategoryViewRepository;
import by.cryptic.utils.handler.QueryHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class CategoryGetByIdQueryHandler implements QueryHandler<UUID, CategoryDTO> {

    private final CategoryViewRepository categoryViewRepository;
    private final CacheManager cacheManager;

    @Override
    public CategoryDTO handle(UUID id) {
        return findInCacheOrDB(id);
    }

    public CategoryDTO findInCacheOrDB(UUID id) {
        String cacheKey = "category:" + id;
        Cache cache = cacheManager.getCache("categories");
        if (cache != null) {
            Category cachedCategory = cache.get(cacheKey, Category.class);
            if (cachedCategory != null) {
                log.debug("Category was found in cache {}", cachedCategory);
                return CategoryMapper.toDto(cachedCategory);
            }
        }

        CategoryView dbCategory = categoryViewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with id %s was not found"
                        .formatted(id)));
        if (cache != null) {
            cache.put(cacheKey, dbCategory);
        }
        return CategoryMapper.toDto(dbCategory);
    }
}
