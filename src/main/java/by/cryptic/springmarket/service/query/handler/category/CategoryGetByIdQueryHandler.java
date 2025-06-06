package by.cryptic.springmarket.service.query.handler.category;

import by.cryptic.springmarket.mapper.CategoryMapper;
import by.cryptic.springmarket.repository.read.CategoryViewRepository;
import by.cryptic.springmarket.service.query.CategoryDTO;
import by.cryptic.springmarket.service.query.handler.QueryHandler;
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
