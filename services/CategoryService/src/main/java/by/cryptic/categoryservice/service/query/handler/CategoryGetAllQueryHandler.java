package by.cryptic.categoryservice.service.query.handler;
import by.cryptic.categoryservice.dto.CategoryDTO;
import by.cryptic.categoryservice.mapper.CategoryMapper;
import by.cryptic.categoryservice.repository.read.CategoryViewRepository;
import by.cryptic.categoryservice.service.query.CategoryGetAllQuery;
import by.cryptic.utils.QueryHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryGetAllQueryHandler implements QueryHandler<CategoryGetAllQuery, List<CategoryDTO>> {

    private final CategoryViewRepository categoryViewRepository;

    @Override
    public List<CategoryDTO> handle(CategoryGetAllQuery command) {
        List<CategoryDTO> categories = categoryViewRepository.findAll()
                .stream().map(CategoryMapper::toDto).toList();

        if(categories.isEmpty()) {
            throw new EntityNotFoundException("There are no categories");
        }
        return categories;
    }
}
