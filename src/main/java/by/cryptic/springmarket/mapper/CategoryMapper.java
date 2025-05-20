package by.cryptic.springmarket.mapper;

import by.cryptic.springmarket.dto.CategoryDTO;
import by.cryptic.springmarket.dto.ShortCategoryDTO;
import by.cryptic.springmarket.dto.UpdateReviewDTO;
import by.cryptic.springmarket.model.Category;
import by.cryptic.springmarket.model.Review;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toEntity(CategoryDTO categoryDTO);

    CategoryDTO toDto(Category category);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Category category, CategoryDTO categoryDTO);
}
