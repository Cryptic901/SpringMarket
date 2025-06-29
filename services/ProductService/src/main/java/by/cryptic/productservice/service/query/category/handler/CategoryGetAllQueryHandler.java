package by.cryptic.productservice.service.query.category.handler;
import by.cryptic.productservice.dto.CategoryDTO;
import by.cryptic.productservice.mapper.CategoryMapper;
import by.cryptic.productservice.repository.read.CategoryViewRepository;
import by.cryptic.productservice.service.query.category.CategoryGetAllQuery;
import by.cryptic.utils.QueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryGetAllQueryHandler implements QueryHandler<CategoryGetAllQuery, List<CategoryDTO>> {


    private final CategoryViewRepository categoryViewRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDTO> handle(CategoryGetAllQuery command) {
        return categoryViewRepository.findAll().stream().map(categoryMapper::toDto).toList();
    }
}
