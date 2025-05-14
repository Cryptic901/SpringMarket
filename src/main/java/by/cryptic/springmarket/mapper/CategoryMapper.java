package by.cryptic.springmarket.mapper;

import by.cryptic.springmarket.dto.CategoryDTO;
import by.cryptic.springmarket.dto.FullProductDto;
import by.cryptic.springmarket.model.Category;
import by.cryptic.springmarket.model.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toEntity(CategoryDTO categoryDTO);

    CategoryDTO toDto(Category category);
}
