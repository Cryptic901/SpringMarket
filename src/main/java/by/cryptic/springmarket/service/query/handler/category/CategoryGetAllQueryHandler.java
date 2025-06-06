package by.cryptic.springmarket.service.query.handler.category;

import by.cryptic.springmarket.mapper.CategoryMapper;
import by.cryptic.springmarket.repository.read.CategoryViewRepository;
import by.cryptic.springmarket.service.query.CategoryDTO;
import by.cryptic.springmarket.service.query.CategoryGetAllQuery;
import by.cryptic.springmarket.service.query.handler.QueryHandler;
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
