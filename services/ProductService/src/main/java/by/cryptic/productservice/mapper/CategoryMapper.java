package by.cryptic.productservice.mapper;

import by.cryptic.productservice.dto.CategoryDTO;
import by.cryptic.productservice.dto.ShortCategoryDTO;
import by.cryptic.utils.event.category.CategoryUpdatedEvent;
import by.cryptic.productservice.model.read.CategoryView;
import by.cryptic.productservice.model.write.Category;
import by.cryptic.productservice.service.command.category.CategoryUpdateCommand;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDTO toDto(CategoryView categoryView);

    CategoryDTO toDto(Category category);

    ShortCategoryDTO toShortDto(Category category);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Category category, CategoryUpdateCommand categoryDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateView(@MappingTarget CategoryView categoryView, CategoryUpdatedEvent event);
}
