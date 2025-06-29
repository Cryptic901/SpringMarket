package by.cryptic.productservice.mapper;

import by.cryptic.productservice.dto.ShortCategoryDTO;
import by.cryptic.productservice.model.write.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShortCategoryMapper {

    Category toEntity(ShortCategoryDTO categoryDTO);

    ShortCategoryDTO toDto(Category category);
}
