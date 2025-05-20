package by.cryptic.springmarket.service;

import by.cryptic.springmarket.dto.CategoryDTO;
import by.cryptic.springmarket.mapper.CategoryMapper;
import by.cryptic.springmarket.model.Category;
import by.cryptic.springmarket.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll().stream().map(categoryMapper::toDto).toList();
    }

    public CategoryDTO findById(UUID id) {
        return categoryMapper.toDto(categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: %s".formatted(id))));
    }

    @Transactional
    public CategoryDTO save(CategoryDTO categoryDTO) {
        return categoryMapper.toDto(categoryRepository.save(categoryMapper.toEntity(categoryDTO)));
    }

    @Transactional
    public CategoryDTO update(UUID id ,CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: %s".formatted(id)));
        categoryMapper.updateEntity(category,categoryDTO);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Transactional
    public void delete(UUID id) {
        categoryRepository.deleteById(id);
    }
}
