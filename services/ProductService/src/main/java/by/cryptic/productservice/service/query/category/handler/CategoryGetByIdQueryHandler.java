package by.cryptic.productservice.service.query.category.handler;

import by.cryptic.productservice.dto.CategoryDTO;
import by.cryptic.productservice.mapper.CategoryMapper;
import by.cryptic.productservice.repository.read.CategoryViewRepository;
import by.cryptic.utils.QueryHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CategoryGetByIdQueryHandler implements QueryHandler<UUID, CategoryDTO> {


    private final CategoryMapper categoryMapper;
    private final CategoryViewRepository categoryViewRepository;

    public CategoryDTO handle(UUID command) {
        return categoryMapper.toDto(categoryViewRepository.findById(command)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: %s".formatted(command))));
    }
}
