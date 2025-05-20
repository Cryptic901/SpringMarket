package by.cryptic.springmarket.mapper;

import by.cryptic.springmarket.dto.ShortCategoryDTO;
import by.cryptic.springmarket.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShortCategoryMapper {

    Category toEntity(ShortCategoryDTO categoryDTO);

    ShortCategoryDTO toDto(Category category);
}
